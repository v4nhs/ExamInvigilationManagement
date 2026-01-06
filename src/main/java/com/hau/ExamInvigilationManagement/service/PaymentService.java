package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.dto.response.PaymentResponse;
import com.hau.ExamInvigilationManagement.dto.response.SalaryResponse;
import com.hau.ExamInvigilationManagement.entity.ExamSchedule;
import com.hau.ExamInvigilationManagement.entity.Lecturer;
import com.hau.ExamInvigilationManagement.entity.Payment;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Optional<Payment> findByLecturer(Lecturer lecturer);
    PaymentResponse getById(Long id);

    List<PaymentResponse> getAll();

    void delete(Long id);

    void calculatePayment(ExamSchedule exam, Lecturer lecturer, Long studentAssigned);

    SalaryResponse getSalaryByLecturer(Long lecturerId);
    void revokePayment(ExamSchedule exam, Lecturer lecturer);
}
