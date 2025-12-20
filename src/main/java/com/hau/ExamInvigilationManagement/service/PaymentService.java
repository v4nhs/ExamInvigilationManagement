package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.dto.request.PaymentRequest;
import com.hau.ExamInvigilationManagement.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    PaymentResponse create(PaymentRequest request);
    List<PaymentResponse> getAll();
    PaymentResponse getById(Long id);
    void delete(Long id);
}
