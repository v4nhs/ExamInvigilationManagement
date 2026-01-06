package com.hau.ExamInvigilationManagement.controller;

import com.hau.ExamInvigilationManagement.dto.request.AssignLecturerRequest;
import com.hau.ExamInvigilationManagement.dto.request.CreateExamScheduleRequest;
import com.hau.ExamInvigilationManagement.service.ExamScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/exam-schedules")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ExamScheduleController {

    private final ExamScheduleService service;
    @PostMapping(value = "/import", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importSchedule(@RequestParam("file") MultipartFile file) {
        service.importExamSchedule(file);
        return ResponseEntity.ok(Collections.singletonMap("message", "Import lịch thi thành công!"));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateExamScheduleRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/{id}/assign-written")
    public ResponseEntity<?> assignWritten(@PathVariable Long id, @RequestBody AssignLecturerRequest req) {
        service.assignWrittenExam(id, req.getLecturerIds(), req.getRoom(), req.getStudentCount());
        return ResponseEntity.ok(Collections.singletonMap("message", "Phân công & Cập nhật thành công"));
    }
    // ✅ API 2: Phân công thi KHÁC (Vấn đáp/Thực hành...)
    @PostMapping("/{id}/assign-other")
    public ResponseEntity<?> assignOther(@PathVariable Long id, @RequestBody AssignLecturerRequest req) {
        // Truyền thêm req.getStudentCount()
        service.assignNonWrittenExam(id, req.getLecturerIds(), req.getRoom(), req.getStudentCount());
        return ResponseEntity.ok(Collections.singletonMap("message", "Phân công & Cập nhật thành công"));
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
