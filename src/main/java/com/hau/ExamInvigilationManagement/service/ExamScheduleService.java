package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.dto.request.CreateExamScheduleRequest;
import com.hau.ExamInvigilationManagement.dto.response.ExamScheduleResponse;
import com.hau.ExamInvigilationManagement.dto.response.LecturerResponse;

import java.util.List;

public interface ExamScheduleService {
    ExamScheduleResponse create(CreateExamScheduleRequest request);
    List<ExamScheduleResponse> getAll();

    void assignLecturers(Long examScheduleId, List<Long> lecturerIds);

    List<LecturerResponse> getAvailableLecturers(Long examScheduleId);
}
