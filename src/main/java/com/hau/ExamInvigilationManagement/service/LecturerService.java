package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.dto.request.LecturerRequest;
import com.hau.ExamInvigilationManagement.dto.response.LecturerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LecturerService {
    LecturerResponse create(LecturerRequest request);
    List<LecturerResponse> getAll();
    LecturerResponse getById(Long id);
    LecturerResponse update(Long id, LecturerRequest request);
    List<LecturerResponse> getAvailableLecturers(Long examScheduleId);
    void delete(Long id);
    Page<LecturerResponse> getAllWithPagination(Pageable pageable);
    Page<LecturerResponse> searchByKeyword(String keyword, Pageable pageable);
}
