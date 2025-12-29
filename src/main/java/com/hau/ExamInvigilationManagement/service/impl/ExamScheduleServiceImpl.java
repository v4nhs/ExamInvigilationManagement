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

        if (lecturerIds.size() != exam.getInvigilatorCount()) {
            throw new AppException(ErrorCode.INVALID_INVIGILATOR_COUNT);
        }

        // ❌ kiểm tra trùng lịch TRƯỚC
        for (Long lecturerId : lecturerIds) {
            Lecturer lecturer = lecturerRepo.findById(lecturerId)
                    .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND));

            boolean conflict = assignmentRepo
                    .existsConflict(
                            lecturer,
                            exam.getExamDate(),
                            exam.getExamTime(),
                            exam.getId()
                    );

            if (conflict) {
                throw new AppException(ErrorCode.LECTURER_CONFLICT);
            }
        }

        // 🔹 chia sinh viên
        int totalStudents = exam.getStudentCount();
        int totalLecturers = lecturerIds.size();
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
}