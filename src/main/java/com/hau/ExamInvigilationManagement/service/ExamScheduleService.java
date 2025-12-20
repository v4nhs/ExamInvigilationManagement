package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.dto.request.CreateExamScheduleRequest;
import com.hau.ExamInvigilationManagement.entity.ExamSchedule;

import java.util.List;

public interface ExamScheduleService {

    ExamSchedule create(CreateExamScheduleRequest request);

    List<ExamSchedule> getAll();
}
