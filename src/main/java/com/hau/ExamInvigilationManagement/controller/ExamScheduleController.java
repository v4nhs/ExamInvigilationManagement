package com.hau.ExamInvigilationManagement.controller;

import com.hau.ExamInvigilationManagement.dto.request.AssignLecturerRequest;
import com.hau.ExamInvigilationManagement.dto.request.CreateExamScheduleRequest;
import com.hau.ExamInvigilationManagement.service.ExamScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/exam-schedules")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ExamScheduleController {

    private final ExamScheduleService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateExamScheduleRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<?> assignLecturers(
            @PathVariable Long id,
            @RequestBody AssignLecturerRequest request
    ) {
        service.assignLecturers(id, request.getLecturerIds());
        Map<String, String> response = Collections.singletonMap("message", "Phân công giảng viên thành công");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/available-lecturers")
    public ResponseEntity<?> getAvailableLecturers(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAvailableLecturers(id));
    }

    @DeleteMapping("/{id}/assign/{lecturerId}")
    public ResponseEntity<?> unassignLecturer(
            @PathVariable Long id,
            @PathVariable Long lecturerId
    ) {
        service.unassignLecturer(id, lecturerId);

        return ResponseEntity.ok(
                Collections.singletonMap("message", "Xóa phân công giảng viên thành công")
        );
    }
}
