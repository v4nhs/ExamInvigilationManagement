package com.hau.ExamInvigilationManagement.service.impl;

import com.hau.ExamInvigilationManagement.dto.request.CreateExamScheduleRequest;
import com.hau.ExamInvigilationManagement.dto.response.ExamScheduleResponse;
import com.hau.ExamInvigilationManagement.entity.Assignment;
import com.hau.ExamInvigilationManagement.entity.Course;
import com.hau.ExamInvigilationManagement.entity.ExamSchedule;
import com.hau.ExamInvigilationManagement.entity.Lecturer;
import com.hau.ExamInvigilationManagement.exception.AppException;
import com.hau.ExamInvigilationManagement.exception.ErrorCode;
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

    @Override
    public ExamScheduleResponse create(CreateExamScheduleRequest req) {

        Course course = courseRepo.findById(req.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        LocalTime examTime = LocalTime.parse(req.getExamTime());
        ExamSchedule exam = ExamSchedule.builder()
                .course(course)
                .examDate(req.getExamDate())
                .examTime(examTime)
                .examDay(req.getExamDay())
                .examType(req.getExamType())
                .studentCount(req.getStudentCount())
                .invigilatorCount(req.getInvigilatorCount())
                .build();

        exam = examRepo.save(exam);

        // 🔥 chọn giảng viên không trùng lịch
        List<Lecturer> available =
                lecturerRepo.findAvailableLecturers(
                        exam.getExamDate(),
                        exam.getExamTime()
                );

        List<Lecturer> selected = available
                .stream()
                .limit(exam.getInvigilatorCount())
                .toList();

        for (Lecturer l : selected) {
            assignmentRepo.save(
                    Assignment.builder()
                            .lecturer(l)
                            .examSchedule(exam)
                            .build()
            );

            // 🔥 tạo payment tự động
            paymentService.createPayment(exam, l);
        }

        return ExamScheduleResponse.from(exam);
    }

    @Override
    public List<ExamScheduleResponse> getAll() {
        return examRepo.findAll()
                .stream()
                .map(ExamScheduleResponse::from)
                .toList();
    }
}