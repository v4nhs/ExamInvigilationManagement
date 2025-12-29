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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentServiceImpl implements AssignmentService {

    private final ExamScheduleRepository examRepo;
    private final LecturerRepository lecturerRepo;
    private final AssignmentRepository assignmentRepo;
    private final PaymentService paymentService;

    @Override
    public void assignLecturers(Long examScheduleId, List<Long> lecturerIds) {

        ExamSchedule exam = examRepo.findById(examScheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        for (Long lecturerId : lecturerIds) {

            Lecturer lecturer = lecturerRepo.findById(lecturerId)
                    .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND));

            boolean conflict = assignmentRepo
                    .existsByLecturerAndExamSchedule_ExamDateAndExamSchedule_ExamTime(
                            lecturer,
                            exam.getExamDate(),
                            exam.getExamTime()
                    );

            if (conflict) {
                throw new AppException(ErrorCode.SCHEDULE_CONFLICT);
            }

            assignmentRepo.save(
                    Assignment.builder()
                            .examSchedule(exam)
                            .lecturer(lecturer)
                            .build()
            );

            paymentService.calculatePaymentForLecturer(lecturerId);
        }
    }
}
