package com.hau.ExamInvigilationManagement.controller;

import com.hau.ExamInvigilationManagement.dto.request.CourseRequest;
import com.hau.ExamInvigilationManagement.dto.response.ApiResponse;
import com.hau.ExamInvigilationManagement.dto.response.CourseResponse;
import com.hau.ExamInvigilationManagement.service.CourseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ApiResponse<CourseResponse> create(@RequestBody CourseRequest request) {
        return ApiResponse.success(courseService.create(request));
    }

    @GetMapping
    public ApiResponse<List<CourseResponse>> getAll() {
        return ApiResponse.success(courseService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(courseService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<CourseResponse> update(
            @PathVariable Long id,
            @RequestBody CourseRequest request) {
        return ApiResponse.success(courseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ApiResponse.success(null);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importCourses(
            @RequestParam("departmentId") Long departmentId, // Chọn khoa để import vào
            @RequestParam("file") MultipartFile file
    ) {
        courseService.importCourses(departmentId, file);
        return ResponseEntity.ok(Collections.singletonMap("message", "Import môn học thành công!"));
    }
}

