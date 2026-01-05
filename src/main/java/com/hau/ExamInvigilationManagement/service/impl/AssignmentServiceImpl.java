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

        // ✅ 1. Kiểm tra định mức số lượng
        long assigned = assignmentRepo.countByExamSchedule(exam);
        if (assigned + lecturerIds.size() > exam.getInvigilatorCount()) {
            throw new AppException(ErrorCode.INVALID_INVIGILATOR_COUNT);
        }

        // ✅ 2. CHECK CONFLICT VỚI CÁC CA KHÁC
        for (Long lecturerId : lecturerIds) {
            Lecturer lecturer = lecturerRepo.findById(lecturerId)
                    .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND));

            // Gọi hàm countConflicts đã sửa ở bước trước
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

        // ✅ 3. CHECK TRÙNG TRONG CÙNG CA
        List<Long> alreadyAssignedIds = assignmentRepo.findByExamSchedule(exam)
                .stream()
                .map(a -> a.getLecturer().getId())
                .collect(Collectors.toList());

        for (Long newId : lecturerIds) {
            if (alreadyAssignedIds.contains(newId)) {
                throw new AppException(ErrorCode.LECTURER_ALREADY_ASSIGNED);
            }
        }

        // ✅ 4. THỰC HIỆN PHÂN CÔNG VÀ CHIA SINH VIÊN
        int totalStudents = exam.getStudentCount();
        int totalLecturers = lecturerIds.size();

        if (totalLecturers == 0) return;

        // Tính toán cơ bản: Mỗi người bao nhiêu, dư bao nhiêu
        int base = totalStudents / totalLecturers;
        int remainder = totalStudents % totalLecturers;

        for (int i = 0; i < lecturerIds.size(); i++) {
            Lecturer lecturer = lecturerRepo.findById(lecturerIds.get(i))
                    .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND));

            // Lưu Assignment
            assignmentRepo.save(
                    Assignment.builder()
                            .examSchedule(exam)
                            .lecturer(lecturer)
                            .build()
            );

            // 🔴 SỬA TẠI ĐÂY: Bỏ check WRITTEN, luôn luôn chia sinh viên
            // Logic: Người thứ i (nếu i < số dư) sẽ phải gánh thêm 1 sinh viên lẻ
            long studentAssigned = base + (i < remainder ? 1 : 0);

            // Tính tiền
            paymentService.calculatePayment(exam, lecturer, studentAssigned);
        }
    }
}