package com.hau.ExamInvigilationManagement.controller;

import com.hau.ExamInvigilationManagement.dto.request.AssignmentRequest;
import com.hau.ExamInvigilationManagement.dto.request.CreateExamScheduleRequest;
import com.hau.ExamInvigilationManagement.dto.response.ApiResponse;
import com.hau.ExamInvigilationManagement.dto.response.AssignmentResponse;
import com.hau.ExamInvigilationManagement.service.ExamScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/exam-schedules")
@RequiredArgsConstructor
public class ExamScheduleController {

    private final ExamScheduleService service;

    @PostMapping(value = "/import", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> importSchedule(@RequestParam("file") MultipartFile file) {
        service.importExamSchedule(file);
        return ResponseEntity.ok(ApiResponse.success(Collections.singletonMap("message", "Import lịch thi thành công!")));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody CreateExamScheduleRequest request) {
        // Service đã return ExamScheduleResponse, không cần map lại
        return ResponseEntity.ok(ApiResponse.success(service.create(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT', 'ACCOUNTING')")
    public ResponseEntity<?> getAll() {
        // Service đã return List<ExamScheduleResponse>, không cần map lại
        return ResponseEntity.ok(ApiResponse.success(service.getAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT', 'ACCOUNTING')")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        // Service đã return ExamScheduleResponse, không cần map lại
        return ResponseEntity.ok(ApiResponse.success(service.getById(id)));
    }

    @PostMapping("/{id}/assign-written")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT')")
    public ResponseEntity<?> assignWritten(@PathVariable Long id, @RequestBody AssignmentRequest req) {
        service.assignWrittenExam(id, req.getLecturerIds(), req.getRoom(), req.getStudentCount());
        return ResponseEntity.ok(ApiResponse.success(Collections.singletonMap("message", "Phân công & Cập nhật thành công")));
    }

    @PostMapping("/{id}/assign-other")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT')")
    public ResponseEntity<?> assignOther(@PathVariable Long id, @RequestBody AssignmentRequest req) {
        service.assignNonWrittenExam(id, req.getLecturerIds(), req.getRoom(), req.getStudentCount());
        return ResponseEntity.ok(ApiResponse.success(Collections.singletonMap("message", "Phân công & Cập nhật thành công")));
    }

    @GetMapping("/{id}/assignments")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getAssignmentsForSchedule(@PathVariable Long id) {
        List<AssignmentResponse> assignments = service.getAssignmentsForSchedule(id);
        return ResponseEntity.ok(ApiResponse.success(assignments));
    }

    @PutMapping("/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT')")
    public ResponseEntity<String> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestParam(required = false) String room) {
        service.updateAssignment(assignmentId, room);
        return ResponseEntity.ok("Cập nhật phân công thành công");
    }

    @DeleteMapping("/{assignmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT')")
    public ResponseEntity<String> deleteAssignment(@PathVariable Long assignmentId) {
        service.deleteAssignment(assignmentId);
        return ResponseEntity.ok("Xóa phân công thành công");
    }
    @GetMapping("/{id}/available-lecturers")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT')")
    public ResponseEntity<?> getAvailableLecturers(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.getAvailableLecturers(id)));
    }

    @GetMapping("/{id}/assigned-lecturers")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT')")
    public ResponseEntity<?> getAssignedLecturers(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.getAssignedLecturers(id)));
    }

    @DeleteMapping("/{id}/assign/{lecturerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT')")
    public ResponseEntity<?> unassignLecturer(
            @PathVariable Long id,
            @PathVariable Long lecturerId
    ) {
        service.unassignLecturer(id, lecturerId);
        return ResponseEntity.ok(ApiResponse.success(Collections.singletonMap("message", "Xóa phân công giảng viên thành công")));
    }
    @GetMapping("/lecturer/{lecturerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT', 'LECTURER')")
    public ResponseEntity<?> getMyExamAssignments(@PathVariable String lecturerId) {
        return ResponseEntity.ok(ApiResponse.success(
                service.getExamSchedulesByLecturerIdentifier(lecturerId)
        ));
    }


    @GetMapping("/paginated")
    public ResponseEntity<?> getAllWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort.Direction dir = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));
        return ResponseEntity.ok(service.getAllWithPagination(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.searchByKeyword(keyword, pageable));
    }
}
