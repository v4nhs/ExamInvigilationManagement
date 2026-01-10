package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.dto.request.RoleRequest;
import com.hau.ExamInvigilationManagement.dto.response.RoleResponse;
import java.util.List;

public interface RoleService {
    RoleResponse create(RoleRequest request);
    List<RoleResponse> getAll();
    RoleResponse update(Long id, RoleRequest request);
    void delete(Long id);
}