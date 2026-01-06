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

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentDetailRepository paymentDetailRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public Optional<Payment> findByLecturer(Lecturer lecturer) {
        return paymentRepository.findByLecturer(lecturer);
    }

    @Override
    public void calculatePayment(ExamSchedule exam, Lecturer lecturer, Long studentAssigned) {

        // 1. Tính toán số tiền (Amount) và Đơn giá (Unit Price)
        long amount = 0;
        long unitPrice = 0;

        if (exam.getExamType() == ExamType.WRITTEN) {
            // Thi viết: 60k/ca
            unitPrice = 60000L;
            amount = 60000L;
        } else {
            // Thi vấn đáp/thực hành: 9k/sv
            unitPrice = 9000L;

            if (studentAssigned > 0) {
                amount = studentAssigned * unitPrice;
            } else {
                amount = 0L; // Giảng viên phụ -> 0đ
            }
        }

        // 2. Lấy Payment cha hoặc Tạo mới nếu chưa có
        Payment payment = paymentRepository.findByLecturer(lecturer)
                .orElseGet(() -> {
                    Payment newPayment = Payment.builder()
                            .lecturer(lecturer)
                            .totalAmount(0L)
                            .build();
                    return paymentRepository.save(newPayment);
                });

        // 3. Lưu PaymentDetail
        PaymentDetail detail = PaymentDetail.builder()
                .examSchedule(exam)
                .payment(payment)
                .amount(amount)
                .studentCount(studentAssigned)
                .unitPrice(unitPrice)
                .build();

        paymentDetailRepository.save(detail);

        // 4. Cập nhật lại tổng tiền trong bảng Payment cha
        payment.setTotalAmount(payment.getTotalAmount() + amount);
        paymentRepository.save(payment);
    }

    @Override
    public SalaryResponse getSalaryByLecturer(Long lecturerId) {

        Payment payment = paymentRepository.findByLecturer_Id(lecturerId)
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
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        return paymentMapper.toResponse(payment);
    }

    @Override
    public List<PaymentResponse> getAll() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    public void revokePayment(ExamSchedule exam, Lecturer lecturer) {
               PaymentDetail detail = paymentDetailRepository.findByExamAndLecturer(exam, lecturer)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
        Payment payment = detail.getPayment();
        payment.setTotalAmount(payment.getTotalAmount() - detail.getAmount());
        paymentRepository.save(payment);

        paymentDetailRepository.delete(detail);
    }

    @Override
    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }
}