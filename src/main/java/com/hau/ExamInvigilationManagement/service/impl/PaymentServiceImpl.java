package com.hau.ExamInvigilationManagement.service.impl;

import com.hau.ExamInvigilationManagement.dto.response.PaymentResponse;
import com.hau.ExamInvigilationManagement.entity.*;
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
    private final PaymentDetailRepository detailRepo;

    @Override
    @Transactional
    public void calculatePayment(ExamSchedule exam, Lecturer lecturer) {

        long amount;

        if (exam.getExamType() == ExamType.WRITTEN) {
            amount = 60000;
        } else {
            amount = exam.getStudentCount() * 9000L;
        }

        // 🔹 lấy hoặc tạo Payment (theo giảng viên)
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

        // 🔹 lưu chi tiết tiền cho ca thi
        PaymentDetail detail = PaymentDetail.builder()
                .payment(payment)
                .examSchedule(exam)
                .amount(amount)
                .build();

        detailRepo.save(detail);

        // 🔹 cộng dồn tổng tiền
        payment.setTotalAmount(payment.getTotalAmount() + amount);
        paymentRepo.save(payment);
    }

    @Override
    public PaymentResponse createPayment(ExamSchedule exam, Lecturer lecturer) {
        return null;
    }

    @Override
    public PaymentResponse getById(Long id) {
        return null;
    }

    @Override
    public List<PaymentResponse> getAll() {
        return null;
    }

    @Override
    public void delete(Long id) {
        paymentRepo.deleteById(id);
    }

    @Override
    public void calculatePaymentForLecturer(Long lecturerId) {

    }
}