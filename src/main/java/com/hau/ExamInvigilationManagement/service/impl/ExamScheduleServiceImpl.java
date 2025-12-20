package com.hau.ExamInvigilationManagement.service.impl;

import com.hau.ExamInvigilationManagement.dto.request.CreateExamScheduleRequest;
import com.hau.ExamInvigilationManagement.entity.Course;
import com.hau.ExamInvigilationManagement.entity.ExamSchedule;
import com.hau.ExamInvigilationManagement.exception.AppException;
import com.hau.ExamInvigilationManagement.exception.ErrorCode;
import com.hau.ExamInvigilationManagement.repository.CourseRepository;
import com.hau.ExamInvigilationManagement.repository.ExamScheduleRepository;
import com.hau.ExamInvigilationManagement.service.ExamScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamScheduleServiceImpl implements ExamScheduleService {

    private final ExamScheduleRepository examScheduleRepository;
    private final CourseRepository courseRepository;

    @Override
    public ExamSchedule create(CreateExamScheduleRequest request) {

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        ExamSchedule examSchedule = ExamSchedule.builder()
                .course(course)
                .examDay(request.getExamDay())
                .examDate(request.getExamDate())
                .examTime(request.getExamTime())
                .invigilatorCount(request.getInvigilatorCount())
                .build();

        return examScheduleRepository.save(examSchedule);
    }

    @Override
    public List<ExamSchedule> getAll() {
        return examScheduleRepository.findAll();
    }
}
