package com.hau.ExamInvigilationManagement.repository;

import com.hau.ExamInvigilationManagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {}

