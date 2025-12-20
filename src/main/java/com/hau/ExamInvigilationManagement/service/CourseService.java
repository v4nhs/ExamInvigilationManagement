package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.dto.request.CourseRequest;
import com.hau.ExamInvigilationManagement.dto.response.CourseResponse;

import java.util.List;

public interface CourseService {

    CourseResponse create(CourseRequest request);

    List<CourseResponse> getAll();

    CourseResponse getById(Long id);

    CourseResponse update(Long id, CourseRequest request);

    void delete(Long id);
}
