package com.hau.ExamInvigilationManagement.service.impl;

import com.hau.ExamInvigilationManagement.dto.request.CreateExamScheduleRequest;
import com.hau.ExamInvigilationManagement.dto.response.ExamScheduleResponse;
import com.hau.ExamInvigilationManagement.dto.response.LecturerResponse;
import com.hau.ExamInvigilationManagement.entity.*;
import com.hau.ExamInvigilationManagement.exception.AppException;
import com.hau.ExamInvigilationManagement.exception.ErrorCode;
import com.hau.ExamInvigilationManagement.mapper.LecturerMapper;
import com.hau.ExamInvigilationManagement.repository.AssignmentRepository;
import com.hau.ExamInvigilationManagement.repository.CourseRepository;
import com.hau.ExamInvigilationManagement.repository.ExamScheduleRepository;
import com.hau.ExamInvigilationManagement.repository.LecturerRepository;
import com.hau.ExamInvigilationManagement.service.ExamScheduleService;
import com.hau.ExamInvigilationManagement.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@CrossOrigin("*")
public class ExamScheduleServiceImpl implements ExamScheduleService {

    private final ExamScheduleRepository examRepo;
    private final CourseRepository courseRepo;
    private final LecturerRepository lecturerRepo;
    private final AssignmentRepository assignmentRepo;
    private final PaymentService paymentService;
    private final LecturerMapper lecturerMapper;

    @Override
    public ExamScheduleResponse create(CreateExamScheduleRequest req) {
        Course course = courseRepo.findById(req.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        LocalTime startTime = LocalTime.parse(req.getExamTime());
        // Mặc định kết thúc sau 90 phút nếu không có input endTime
        LocalTime endTime = startTime.plusMinutes(90);

        ExamSchedule exam = ExamSchedule.builder()
                .course(course)
                .examDate(req.getExamDate())
                .examTime(startTime)
                .endTime(endTime)
                .examDay(req.getExamDay())
                .room(req.getRoom())
                .examType(req.getExamType())
                .studentCount(req.getStudentCount())
                .invigilatorCount(req.getInvigilatorCount())
                .build();

        return ExamScheduleResponse.from(examRepo.save(exam));
    }
    @Override
    public ExamScheduleResponse getById(Long id) {
        ExamSchedule exam = examRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        return ExamScheduleResponse.from(exam);
    }

    @Override
    public List<ExamScheduleResponse> getAll() {
        return examRepo.findAll()
                .stream()
                .map(ExamScheduleResponse::from)
                .toList();
    }

    @Override
    public List<Long> getAssignedLecturerIds(Long examScheduleId) {
        ExamSchedule exam = examRepo.findById(examScheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        return assignmentRepo.findByExamSchedule(exam)
                .stream()
                .map(a -> a.getLecturer().getId())
                .toList();
    }

    // =========================================================================
    // 1️ PHÂN CÔNG THỦ CÔNG CHO THI VIẾT (WRITTEN)
    // - Lưu phòng, Lưu số lượng SV.
    // - Nhưng tính tiền vẫn là 0 (theo quy tắc thi viết tính theo buổi).
    // =========================================================================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignWrittenExam(Long examId, List<Long> lecturerIds, String room, Integer studentCount) {
        ExamSchedule exam = examRepo.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        if (exam.getExamType() != ExamType.WRITTEN) {
            throw new AppException(ErrorCode.INVALID_EXAM_TYPE);
        }

        // 1. CẬP NHẬT PHÒNG THI & SỐ LƯỢNG SINH VIÊN
        boolean isChanged = false;
        if (room != null && !room.trim().isEmpty()) {
            exam.setRoom(room);
            isChanged = true;
        }
        if (studentCount != null && studentCount >= 0) {
            exam.setStudentCount(studentCount);
            isChanged = true;
        }
        if (isChanged) {
            examRepo.save(exam);
        }

        // 2. Validate & Lưu phân công
        validateAssignmentLimit(exam, lecturerIds.size());

        for (Long lecturerId : lecturerIds) {
            Lecturer lecturer = validateAndGetLecturer(exam, lecturerId);

            assignmentRepo.save(Assignment.builder()
                    .examSchedule(exam)
                    .lecturer(lecturer)
                    .build());

            // TÍNH TIỀN: Thi viết vẫn truyền 0 (tính theo ca), dù có update studentCount vào DB để lưu trữ
            paymentService.calculatePayment(exam, lecturer, 0L);
        }
    }

    // =========================================================================
    // 2️  PHÂN CÔNG THỦ CÔNG CHO THI KHÁC
    // - Lưu phòng, Lưu số lượng SV.
    // - Tính tiền: Người đầu tiên nhận full số SV mới cập nhật.
    // =========================================================================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignNonWrittenExam(Long examId, List<Long> lecturerIds, String room, Integer studentCount) {
        ExamSchedule exam = examRepo.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        if (exam.getExamType() == ExamType.WRITTEN) {
            throw new AppException(ErrorCode.INVALID_EXAM_TYPE);
        }

        // 1. CẬP NHẬT PHÒNG THI & SỐ LƯỢNG SINH VIÊN
        boolean isChanged = false;
        if (room != null && !room.trim().isEmpty()) {
            exam.setRoom(room);
            isChanged = true;
        }
        if (studentCount != null && studentCount >= 0) {
            exam.setStudentCount(studentCount);
            isChanged = true;
        }
        if (isChanged) {
            examRepo.save(exam);
        }

        validateAssignmentLimit(exam, lecturerIds.size());

        // Lấy tổng sinh viên (Ưu tiên số vừa nhập, nếu không nhập thì lấy số cũ trong DB)
        int currentTotalStudents = (exam.getStudentCount() == null) ? 0 : exam.getStudentCount();

        for (int i = 0; i < lecturerIds.size(); i++) {
            Lecturer lecturer = validateAndGetLecturer(exam, lecturerIds.get(i));

            assignmentRepo.save(Assignment.builder()
                    .examSchedule(exam)
                    .lecturer(lecturer)
                    .build());

            // TÍNH TIỀN: Dùng số lượng sinh viên thực tế để tính
            long studentAssigned = 0;
            if (i == 0) {
                studentAssigned = currentTotalStudents; // Người 1 nhận hết
            } else {
                studentAssigned = 0; // Người sau nhận 0
            }
            paymentService.calculatePayment(exam, lecturer, studentAssigned);
        }
    }
    // =========================================================================
    // PRIVATE HELPER METHODS
    // =========================================================================

    private void validateAssignmentLimit(ExamSchedule exam, int newCount) {
        long currentAssignedCount = assignmentRepo.countByExamSchedule(exam);
        if (currentAssignedCount + newCount > exam.getInvigilatorCount()) {
            throw new AppException(ErrorCode.INVALID_INVIGILATOR_COUNT);
        }
    }

    // 🟢 HÀM CHECK LOGIC QUAN TRỌNG NHẤT
    private Lecturer validateAndGetLecturer(ExamSchedule exam, Long lecturerId) {
        // 1. Check trùng trong cùng ca (Duplicate Assignment)
        boolean isAssigned = assignmentRepo.findByExamSchedule(exam).stream()
                .anyMatch(a -> a.getLecturer().getId().equals(lecturerId));

        if (isAssigned) {
            throw new AppException(ErrorCode.LECTURER_ALREADY_ASSIGNED);
        }

        Lecturer lecturer = lecturerRepo.findById(lecturerId)
                .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND));

        // 2. CHECK TRÙNG LỊCH (Time Overlap)
        // Nếu endTime null, mặc định +90 phút
        LocalTime effectiveEndTime = (exam.getEndTime() != null)
                ? exam.getEndTime()
                : exam.getExamTime().plusMinutes(90);

        long conflictCount = assignmentRepo.countTimeOverlaps(
                lecturer,
                exam.getExamDate(),
                exam.getExamTime(),
                effectiveEndTime,
                exam.getId()
        );

        if (conflictCount > 0) {
            throw new AppException(ErrorCode.LECTURER_CONFLICT);
        }

        return lecturer;
    }

    @Override
    public List<LecturerResponse> getAvailableLecturers(Long examScheduleId) {
        ExamSchedule exam = examRepo.findById(examScheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        return lecturerRepo.findAvailableLecturers(
                        exam.getExamDate(),
                        exam.getExamTime()
                )
                .stream()
                .map(lecturerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unassignLecturer(Long examScheduleId, Long lecturerId) {
        // 1. Tìm Ca thi
        ExamSchedule exam = examRepo.findById(examScheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        // 2. Tìm Giảng viên
        Lecturer lecturer = lecturerRepo.findById(lecturerId)
                .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND));

        // 3. Tìm Phân công (Assignment)
        Assignment assignment = assignmentRepo.findByExamScheduleAndLecturer(exam, lecturer)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // 4. Xóa Payment (Thu hồi tiền của người bị xóa)
        paymentService.revokePayment(exam, lecturer);

        // 5. Xóa Assignment (Xóa phân công)
        assignmentRepo.delete(assignment);

        // để câu query tìm danh sách còn lại bên dưới không bị dính người vừa xóa.
        assignmentRepo.flush();

        // 6. TÍNH LẠI TIỀN CHO NHỮNG NGƯỜI CÒN LẠI (Re-calculate)
        // Nếu là thi viết -> Không cần tính lại (vì ai cũng nhận lương cố định theo ca).
        // Nếu là thi Khác (Vấn đáp...) -> Cần tính lại để người thứ 2 lên làm người thứ 1.
        if (exam.getExamType() != ExamType.WRITTEN) {
            recalculateRemainingLecturers(exam);
        }
    }

    // Hàm phụ trợ: Tính lại tiền cho danh sách còn lại
    private void recalculateRemainingLecturers(ExamSchedule exam) {
        List<Assignment> remainingAssignments = assignmentRepo.findByExamSchedule(exam);

        if (remainingAssignments.isEmpty()) return;

        int totalStudents = (exam.getStudentCount() == null) ? 0 : exam.getStudentCount();

        // Duyệt lại danh sách
        for (int i = 0; i < remainingAssignments.size(); i++) {
            Lecturer l = remainingAssignments.get(i).getLecturer();

            // Logic: Người đầu tiên trong danh sách mới sẽ nhận full sinh viên
            long studentAssigned = (i == 0) ? totalStudents : 0;

            // Gọi PaymentService cập nhật lại số tiền
            paymentService.calculatePayment(exam, l, studentAssigned);
        }
    }

    // =========================================================================
    // IMPORT EXCEL (ĐÃ CẬP NHẬT ĐỂ ĐỌC END-TIME)
    // =========================================================================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importExamSchedule(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            System.out.println("============== BẮT ĐẦU IMPORT (FULL LOGIC) ==============");

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) throw new RuntimeException("File Excel thiếu dòng tiêu đề!");

            int colIndexCode = -1;
            int colIndexDay = -1;
            int colIndexDate = -1;
            int colIndexTime = -1;
            int colIndexType = -1;
            int colIndexCount = -1;
            int colIndexRoom = -1;

            for (Cell cell : headerRow) {
                String header = dataFormatter.formatCellValue(cell).toLowerCase().trim();
                int idx = cell.getColumnIndex();

                if (header.contains("mã hp") || header.contains("mã học phần")) colIndexCode = idx;
                else if (header.contains("hình thức") || header.contains("loại thi")) colIndexType = idx;
                else if (header.equals("thứ") || header.startsWith("thứ ")) colIndexDay = idx;
                else if (header.contains("ngày thi") || header.contains("ngày")) colIndexDate = idx;
                else if (header.contains("giờ thi") || header.contains("giờ")) colIndexTime = idx;
                else if (header.contains("số cán bộ") || header.contains("số lượng") || header.equals("sl")) colIndexCount = idx;
                else if (header.contains("phòng")) colIndexRoom = idx;
            }

            if (colIndexCount == -1 && colIndexType == -1) {
                colIndexCount = 6;
                System.out.println("Chế độ tương thích File Cũ (Cột 6 = Số lượng)");
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Row.MissingCellPolicy policy = Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

                // --- Mã HP ---
                String courseCode = "";
                if (colIndexCode != -1) courseCode = dataFormatter.formatCellValue(row.getCell(colIndexCode, policy)).trim();
                if (courseCode.isEmpty()) continue;

                var courseOpt = courseRepo.findByCode(courseCode);
                if (courseOpt.isEmpty()) {
                    System.err.println("Dòng " + (i+1) + ": Không tìm thấy môn " + courseCode);
                    continue;
                }
                Course course = courseOpt.get();

                // --- Ngày ---
                String examDay = (colIndexDay != -1) ? dataFormatter.formatCellValue(row.getCell(colIndexDay, policy)).trim() : "";
                String dateStr = (colIndexDate != -1) ? dataFormatter.formatCellValue(row.getCell(colIndexDate, policy)).trim() : "";
                LocalDate examDate = LocalDate.now();
                try { examDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")); } catch (Exception e) {}

                // --- Giờ (Start & End) ---
                String timeRange = (colIndexTime != -1) ? dataFormatter.formatCellValue(row.getCell(colIndexTime, policy)).trim() : "";
                LocalTime examTime = LocalTime.of(7, 0);
                LocalTime endTime = LocalTime.of(9, 0); // Mặc định +2h nếu lỗi
                try {
                    String[] parts = timeRange.split("-");
                    String s = parts[0].toUpperCase().replace("H", ":").trim();
                    if (s.length() == 4) s = "0" + s;
                    examTime = LocalTime.parse(s);

                    if (parts.length > 1) {
                        String eStr = parts[1].toUpperCase().replace("H", ":").trim();
                        if (eStr.length() == 4) eStr = "0" + eStr;
                        endTime = LocalTime.parse(eStr);
                    } else {
                        endTime = examTime.plusMinutes(90);
                    }
                } catch (Exception e) {}

                // --- Hình Thức ---
                ExamType examType = ExamType.WRITTEN;
                if (colIndexType != -1) {
                    String typeStr = dataFormatter.formatCellValue(row.getCell(colIndexType, policy)).trim();
                    String typeNorm = Normalizer.normalize(typeStr, Normalizer.Form.NFC).toLowerCase();
                    if (typeNorm.contains("viết") || typeNorm.contains("viet")) examType = ExamType.WRITTEN;
                    else if (typeNorm.contains("khác") || typeNorm.contains("vấn đáp")) examType = ExamType.OTHER;
                }

                // --- Số Lượng ---
                int invigilatorCount = 0;
                if (colIndexCount != -1) {
                    String countStr = dataFormatter.formatCellValue(row.getCell(colIndexCount, policy)).trim();
                    try { if (!countStr.isEmpty()) invigilatorCount = (int) Double.parseDouble(countStr); } catch (Exception e) {}
                }

                // --- Phòng ---
                String room = "TBD";
                if (colIndexRoom != -1) {
                    String r = dataFormatter.formatCellValue(row.getCell(colIndexRoom, policy)).trim();
                    if (!r.isEmpty()) room = r;
                }

                // --- Lưu ---
                ExamSchedule exam = ExamSchedule.builder()
                        .course(course)
                        .examDay(examDay)
                        .examDate(examDate)
                        .examTime(examTime)
                        .endTime(endTime) // 🟢 Quan trọng cho check trùng
                        .invigilatorCount(invigilatorCount)
                        .studentCount(0)
                        .examType(examType)
                        .room(room)
                        .build();

                examRepo.save(exam);
            }
            System.out.println("============== IMPORT HOÀN TẤT ==============");
        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file: " + e.getMessage());
        }
    }
}