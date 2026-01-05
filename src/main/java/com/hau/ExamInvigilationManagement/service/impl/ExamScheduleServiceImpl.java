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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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

        ExamSchedule exam = ExamSchedule.builder()
                .course(course)
                .examDate(req.getExamDate())
                .examTime(LocalTime.parse(req.getExamTime()))
                .examDay(req.getExamDay())
                .examType(req.getExamType())
                .studentCount(req.getStudentCount())
                .invigilatorCount(req.getInvigilatorCount())
                .build();

        return ExamScheduleResponse.from(examRepo.save(exam));
    }

    @Override
    public List<ExamScheduleResponse> getAll() {
        return examRepo.findAll()
                .stream()
                .map(ExamScheduleResponse::from)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignLecturers(Long examId, List<Long> lecturerIds) {

        ExamSchedule exam = examRepo.findById(examId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        // ✅ 1. Kiểm tra số lượng (Logic cộng dồn: Đã có + Mới > Định mức)
        long currentAssignedCount = assignmentRepo.countByExamSchedule(exam);
        if (currentAssignedCount + lecturerIds.size() > exam.getInvigilatorCount()) {
            throw new AppException(ErrorCode.INVALID_INVIGILATOR_COUNT);
        }

        // ✅ 2. Lấy danh sách ID đã gán trong ca này (để check trùng lặp)
        List<Long> alreadyAssignedIds = assignmentRepo.findByExamSchedule(exam)
                .stream()
                .map(a -> a.getLecturer().getId())
                .toList();

        // ✅ 3. Vòng lặp kiểm tra Conflict và Trùng lặp
        for (Long lecturerId : lecturerIds) {
            // Check trùng trong cùng ca thi
            if (alreadyAssignedIds.contains(lecturerId)) {
                throw new AppException(ErrorCode.LECTURER_ALREADY_ASSIGNED);
            }

            Lecturer lecturer = lecturerRepo.findById(lecturerId)
                    .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND));

            // Check trùng lịch với ca khác (Sử dụng hàm countConflicts trả về Long)
            long conflictCount = assignmentRepo.countConflicts(
                    lecturer,
                    exam.getExamDate(),
                    exam.getExamTime(),
                    exam.getId()
            );

            if (conflictCount > 0) {
                throw new AppException(ErrorCode.LECTURER_CONFLICT);
            }
        }

        // ✅ 4. Lưu và tính toán thanh toán
        // Lưu ý: Logic chia sinh viên này đang chia đều cho nhóm giảng viên MỚI thêm vào
        int totalStudents = exam.getStudentCount();
        int totalLecturers = lecturerIds.size();

        // Tránh chia cho 0
        if (totalLecturers == 0) return;

        int base = totalStudents / totalLecturers;
        int remainder = totalStudents % totalLecturers;

        for (int i = 0; i < lecturerIds.size(); i++) {

            Lecturer lecturer = lecturerRepo.findById(lecturerIds.get(i))
                    .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND));

            assignmentRepo.save(
                    Assignment.builder()
                            .lecturer(lecturer)
                            .examSchedule(exam)
                            .build()
            );

            long studentAssigned =
                    exam.getExamType() == ExamType.WRITTEN
                            ? 0
                            : base + (i < remainder ? 1 : 0);

            paymentService.calculatePayment(exam, lecturer, studentAssigned);
        }
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
    public void unassignLecturer(Long examScheduleId, Long lecturerId) {

        // 1. Kiểm tra Ca thi có tồn tại không
        ExamSchedule exam = examRepo.findById(examScheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        // 2. Kiểm tra Giảng viên có tồn tại không
        Lecturer lecturer = lecturerRepo.findById(lecturerId)
                .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND));

        // 3. Tìm bản ghi phân công (Assignment)
        Assignment assignment = assignmentRepo.findByExamScheduleAndLecturer(exam, lecturer)
                .orElseThrow(() -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        // 4. Xóa dữ liệu tính tiền (Payment) trước
        // (Bắt buộc phải xóa payment trước khi xóa assignment để tránh ràng buộc khóa ngoại nếu có)
        paymentService.revokePayment(exam, lecturer);

        // 5. Xóa phân công
        assignmentRepo.delete(assignment);
    }
}