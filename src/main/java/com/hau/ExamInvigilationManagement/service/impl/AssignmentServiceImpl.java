package com.hau.ExamInvigilationManagement.service.impl;

import com.hau.ExamInvigilationManagement.entity.Assignment;
import com.hau.ExamInvigilationManagement.entity.ExamSchedule;
import com.hau.ExamInvigilationManagement.entity.Lecturer;
import com.hau.ExamInvigilationManagement.exception.AppException;
import com.hau.ExamInvigilationManagement.exception.ErrorCode;
import com.hau.ExamInvigilationManagement.repository.AssignmentRepository;
import com.hau.ExamInvigilationManagement.repository.ExamScheduleRepository;
import com.hau.ExamInvigilationManagement.repository.LecturerRepository;
import com.hau.ExamInvigilationManagement.service.AssignmentService;
import com.hau.ExamInvigilationManagement.service.PaymentService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class AssignmentServiceImpl implements AssignmentService {

    private final ExamScheduleRepository examRepo;
    private final LecturerRepository lecturerRepo;
    private final AssignmentRepository assignmentRepo;
    private final PaymentService paymentService;

    @Override
    public void assignLecturers(Long examScheduleId, List<Long> lecturerIds) {

        ExamSchedule exam = examRepo.findById(examScheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        // 1. Kiểm tra định mức số lượng
        long assignedCount = assignmentRepo.countByExamSchedule(exam);
        if (assignedCount + lecturerIds.size() > exam.getInvigilatorCount()) {
            throw new AppException(ErrorCode.INVALID_INVIGILATOR_COUNT);
        }

        // Chuẩn bị giờ bắt đầu và kết thúc để check trùng lịch
        LocalTime start = exam.getExamTime();
        // Nếu endTime null thì mặc định +90 phút để check
        LocalTime end = (exam.getEndTime() != null) ? exam.getEndTime() : start.plusMinutes(90);

        // Danh sách giảng viên hợp lệ để gán
        List<Lecturer> validLecturers = new ArrayList<>();

        // 2. VALIDATE TỪNG GIẢNG VIÊN (Check tồn tại, Check trùng ca, Check trùng lịch)
        for (Long lecturerId : lecturerIds) {
            Lecturer lecturer = lecturerRepo.findById(lecturerId)
                    .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND));

            // A. Check trùng trong cùng ca (Duplicate)
            boolean alreadyInExam = assignmentRepo.existsByExamScheduleAndLecturer(exam, lecturer);
            if (alreadyInExam) {
                throw new AppException(ErrorCode.LECTURER_ALREADY_ASSIGNED);
            }

            // B. Check trùng lịch với ca khác (Time Overlap)
            // Gọi hàm countTimeOverlaps với đủ 5 tham số: Giảng viên, Ngày, Start, End, ID ca hiện tại
            long conflictCount = assignmentRepo.countTimeOverlaps(
                    lecturer,
                    exam.getExamDate(),
                    start,
                    end,
                    exam.getId()
            );

            if (conflictCount > 0) {
                throw new AppException(ErrorCode.LECTURER_CONFLICT);
            }

            // Nếu qua hết các bài test thì thêm vào danh sách
            validLecturers.add(lecturer);
        }

        // 3. THỰC HIỆN PHÂN CÔNG VÀ TÍNH TIỀN
        int totalStudents = (exam.getStudentCount() == null) ? 0 : exam.getStudentCount();
        int totalNewLecturers = validLecturers.size();

        if (totalNewLecturers == 0) return;

        // Tính toán chia sinh viên: Mỗi người bao nhiêu, dư bao nhiêu
        int base = totalStudents / totalNewLecturers;
        int remainder = totalStudents % totalNewLecturers;

        for (int i = 0; i < totalNewLecturers; i++) {
            Lecturer lecturer = validLecturers.get(i);

            // Lưu Assignment
            assignmentRepo.save(
                    Assignment.builder()
                            .examSchedule(exam)
                            .lecturer(lecturer)
                            .build()
            );

            // Logic chia sinh viên: Người thứ i (nếu i < số dư) sẽ gánh thêm 1 sinh viên lẻ
            long studentAssigned = base + (i < remainder ? 1 : 0);

            // Tính tiền
            paymentService.calculatePayment(exam, lecturer, studentAssigned);
        }
    }
}
