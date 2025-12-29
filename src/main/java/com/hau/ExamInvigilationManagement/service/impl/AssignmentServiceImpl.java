package com.hau.ExamInvigilationManagement.service.impl;

import com.hau.ExamInvigilationManagement.entity.Assignment;
import com.hau.ExamInvigilationManagement.entity.ExamSchedule;
import com.hau.ExamInvigilationManagement.entity.ExamType;
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

        long assigned = assignmentRepo.countByExamSchedule(exam);
        if (assigned + lecturerIds.size() > exam.getInvigilatorCount()) {
            throw new AppException(ErrorCode.INVALID_INVIGILATOR_COUNT);
        }

        // 🔴 CHECK CONFLICT TRƯỚC → nếu 1 người trùng → rollback toàn bộ
        for (Long lecturerId : lecturerIds) {

            Lecturer lecturer = lecturerRepo.findById(lecturerId)
                    .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND));

            boolean conflict = assignmentRepo.existsConflict(
                    lecturer,
                    exam.getExamDate(),
                    exam.getExamTime(),
                    exam.getId()
            );

            if (conflict) {
                throw new AppException(ErrorCode.LECTURER_CONFLICT);
            }
        }

        // 🔹 chia sinh viên (OTHER)
        int totalStudents = exam.getStudentCount();
        int totalLecturers = lecturerIds.size();
        int base = totalStudents / totalLecturers;
        int remainder = totalStudents % totalLecturers;

        for (int i = 0; i < lecturerIds.size(); i++) {

            Lecturer lecturer = lecturerRepo.findById(lecturerIds.get(i))
                    .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND));

            assignmentRepo.save(
                    Assignment.builder()
                            .examSchedule(exam)
                            .lecturer(lecturer)
                            .build()
            );

            long studentAssigned =
                    exam.getExamType() == ExamType.WRITTEN
                            ? 0
                            : base + (i < remainder ? 1 : 0);

            paymentService.calculatePayment(exam, lecturer, studentAssigned);
        }
    }
}


