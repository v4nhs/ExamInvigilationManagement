package com.hau.ExamInvigilationManagement.service.impl;

import com.hau.ExamInvigilationManagement.dto.response.PaymentDetailResponse;
import com.hau.ExamInvigilationManagement.dto.response.PaymentResponse;
import com.hau.ExamInvigilationManagement.dto.response.SalaryResponse;
import com.hau.ExamInvigilationManagement.entity.*;
import com.hau.ExamInvigilationManagement.exception.AppException;
import com.hau.ExamInvigilationManagement.exception.ErrorCode;
import com.hau.ExamInvigilationManagement.mapper.PaymentMapper;
import com.hau.ExamInvigilationManagement.repository.PaymentDetailRepository;
import com.hau.ExamInvigilationManagement.repository.PaymentRepository;
import com.hau.ExamInvigilationManagement.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepo;
    private final PaymentDetailRepository paymentDetailRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public void calculatePayment(
            ExamSchedule exam,
            Lecturer lecturer,
            long studentAssigned
    ) {

        long unitPrice;
        long amount;

        if (exam.getExamType() == ExamType.WRITTEN) {
            unitPrice = 60000;
            amount = 60000;
        } else {
            unitPrice = 9000;
            amount = studentAssigned * unitPrice;
        }

        Payment payment = paymentRepo.findByLecturer(lecturer)
                .orElseGet(() ->
                        paymentRepo.save(
                                Payment.builder()
                                        .lecturer(lecturer)
                                        .totalAmount(0L)
                                        .status(PaymentStatus.UNPAID)
                                        .build()
                        )
                );

        PaymentDetail detail = PaymentDetail.builder()
                .payment(payment)
                .examSchedule(exam)
                .examType(exam.getExamType())
                .studentCount(studentAssigned)
                .unitPrice(unitPrice)
                .amount(amount)
                .build();

        paymentDetailRepository.save(detail);

        payment.setTotalAmount(
                payment.getTotalAmount() + amount
        );

        paymentRepo.save(payment);
    }

    @Override
    public SalaryResponse getSalaryByLecturer(Long lecturerId) {

        Payment payment = paymentRepo.findByLecturer_Id(lecturerId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        return SalaryResponse.builder()
                .lecturerId(payment.getLecturer().getId())
                .lecturerName(payment.getLecturer().getFullName())
                .totalAmount(payment.getTotalAmount())
                .details(
                        payment.getDetails().stream()
                                .map(d -> PaymentDetailResponse.builder()
                                        .examScheduleId(d.getExamSchedule().getId())
                                        .examType(d.getExamType())
                                        .studentCount(d.getStudentCount())
                                        .unitPrice(d.getUnitPrice())
                                        .amount(d.getAmount())
                                        .build())
                                .toList()
                )
                .build();
    }
    @Override
    public PaymentResponse getById(Long id) {
        Payment payment = paymentRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        return paymentMapper.toResponse(payment);
    }

    @Override
    public List<PaymentResponse> getAll() {
        return paymentRepo.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    public void revokePayment(ExamSchedule exam, Lecturer lecturer) {
        PaymentDetail detail = paymentDetailRepository.findByExamAndLecturer(exam, lecturer)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
        paymentDetailRepository.delete(detail);    }
    @Override
    public void delete(Long id) {
        paymentRepo.deleteById(id);
    }
}