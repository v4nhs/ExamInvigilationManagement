package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.dto.request.DepartmentRequest;
import com.hau.ExamInvigilationManagement.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {

    DepartmentResponse create(DepartmentRequest request);

    DepartmentResponse update(Long id, DepartmentRequest request);

    void delete(Long id);

    DepartmentResponse getById(Long id);

    List<DepartmentResponse> getAll();
}
