package com.hau.ExamInvigilationManagement.controller;

import com.hau.ExamInvigilationManagement.dto.request.PaymentRequest;
import com.hau.ExamInvigilationManagement.dto.response.ApiResponse;
import com.hau.ExamInvigilationManagement.dto.response.PaymentResponse;
import com.hau.ExamInvigilationManagement.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTING')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Sort.Direction sortDir = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sort));
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentsPaginated(pageable)));
    }

    @GetMapping("/{id}")
    public ApiResponse<PaymentResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(paymentService.getById(id));
    }

    @GetMapping("/lecturers/{lecturerId}")
    public ResponseEntity<?> getSalary(@PathVariable Long lecturerId) {
        return ResponseEntity.ok(paymentService.getSalaryByLecturer(lecturerId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        paymentService.delete(id);
        return ApiResponse.success(null);
    }
}
