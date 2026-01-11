package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.dto.request.CourseRequest;
import com.hau.ExamInvigilationManagement.dto.response.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseService {

    CourseResponse create(CourseRequest request);

    List<CourseResponse> getAll();

    CourseResponse getById(Long id);

    CourseResponse update(Long id, CourseRequest request);

    void delete(Long id);
    void importCourses(Long departmentId, MultipartFile file);
    Page<CourseResponse> getAllWithPagination(Pageable pageable);
    Page<CourseResponse> searchByKeyword(String keyword, Pageable pageable);
}
