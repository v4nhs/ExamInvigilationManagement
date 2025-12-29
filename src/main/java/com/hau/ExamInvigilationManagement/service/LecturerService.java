package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.dto.request.LecturerRequest;
import com.hau.ExamInvigilationManagement.dto.response.LecturerResponse;

import java.util.List;

public interface LecturerService {
    LecturerResponse create(LecturerRequest request);
    List<LecturerResponse> getAll();
    LecturerResponse getById(Long id);
    LecturerResponse update(Long id, LecturerRequest request);
    List<LecturerResponse> getAvailableLecturers(Long examScheduleId);
    void delete(Long id);
}
