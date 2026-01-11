package com.hau.ExamInvigilationManagement.controller;

import com.hau.ExamInvigilationManagement.dto.request.DepartmentRequest;
import com.hau.ExamInvigilationManagement.dto.response.ApiResponse;
import com.hau.ExamInvigilationManagement.dto.response.DepartmentResponse;
import com.hau.ExamInvigilationManagement.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService service;

    @PostMapping
    public ApiResponse<DepartmentResponse> create(
            @RequestBody @Valid DepartmentRequest request) {
        return ApiResponse.success(service.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<DepartmentResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid DepartmentRequest request) {
        return ApiResponse.success(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<DepartmentResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(service.getById(id));
    }

    @GetMapping
    public ApiResponse<List<DepartmentResponse>> getAll() {
        return ApiResponse.success(service.getAll());
    }

    @GetMapping("/paginated")
    public ApiResponse<Page<DepartmentResponse>> getDepartmentsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        return ApiResponse.success(service.getAllWithPagination(pageable));
    }

    @GetMapping("/search")
    public ApiResponse<Page<DepartmentResponse>> searchDepartments(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(service.searchByKeyword(keyword, pageable));
    }
}
