package com.hau.ExamInvigilationManagement.service.impl;

import com.hau.ExamInvigilationManagement.dto.request.PaymentRequest;
import com.hau.ExamInvigilationManagement.dto.response.PaymentResponse;
import com.hau.ExamInvigilationManagement.entity.Lecturer;
import com.hau.ExamInvigilationManagement.entity.Payment;
import com.hau.ExamInvigilationManagement.exception.AppException;
import com.hau.ExamInvigilationManagement.exception.ErrorCode;
import com.hau.ExamInvigilationManagement.mapper.PaymentMapper;
import com.hau.ExamInvigilationManagement.repository.LecturerRepository;
import com.hau.ExamInvigilationManagement.repository.PaymentRepository;
import com.hau.ExamInvigilationManagement.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepo;
    private final LecturerRepository lecturerRepo;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentResponse create(PaymentRequest request) {
        Lecturer lecturer = lecturerRepo.findById(request.getLecturerId())
                .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND));

        Payment payment = Payment.builder()
                .amount(request.getAmount())
                .paymentDate(request.getPaymentDate())
                .lecturer(lecturer)
                .status("PAID")
                .build();

        return paymentMapper.toResponse(paymentRepo.save(payment));
    }

    @Override
    public List<PaymentResponse> getAll() {
        return paymentRepo.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    public PaymentResponse getById(Long id) {
        return paymentMapper.toResponse(
                paymentRepo.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND))
        );
    }

    @Override
    public void delete(Long id) {
        paymentRepo.deleteById(id);
    }
}
