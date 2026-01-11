package com.hau.ExamInvigilationManagement.controller;

import com.hau.ExamInvigilationManagement.dto.request.LecturerRequest;
import com.hau.ExamInvigilationManagement.dto.response.ApiResponse;
import com.hau.ExamInvigilationManagement.dto.response.LecturerResponse;
import com.hau.ExamInvigilationManagement.service.LecturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lecturers")
@RequiredArgsConstructor
public class LecturerController {

    private final LecturerService lecturerService;

    @PostMapping
    public ApiResponse<LecturerResponse> create(@RequestBody LecturerRequest request) {
        return ApiResponse.success(lecturerService.create(request));
    }

    @GetMapping
    public ApiResponse<List<LecturerResponse>> getAll() {
        return ApiResponse.success(lecturerService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<LecturerResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(lecturerService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<LecturerResponse> update(
            @PathVariable Long id,
            @RequestBody LecturerRequest request) {
        return ApiResponse.success(lecturerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        lecturerService.delete(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/paginated")
    public ApiResponse<Page<LecturerResponse>> getLecturersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        return ApiResponse.success(lecturerService.getAllWithPagination(pageable));
    }

    @GetMapping("/search")
    public ApiResponse<Page<LecturerResponse>> searchLecturers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(lecturerService.searchByKeyword(keyword, pageable));
    }
}
