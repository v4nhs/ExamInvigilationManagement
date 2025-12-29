package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.dto.response.PaymentResponse;
import com.hau.ExamInvigilationManagement.entity.ExamSchedule;
import com.hau.ExamInvigilationManagement.entity.Lecturer;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PaymentService {

    @Transactional
    void calculatePayment(ExamSchedule exam, Lecturer lecturer);

    PaymentResponse createPayment(ExamSchedule exam, Lecturer lecturer);

    PaymentResponse getById(Long id);

    List<PaymentResponse> getAll();

    void delete(Long id);
    void calculatePaymentForLecturer(Long lecturerId);
}
