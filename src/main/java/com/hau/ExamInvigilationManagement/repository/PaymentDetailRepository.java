package com.hau.ExamInvigilationManagement.repository;

import com.hau.ExamInvigilationManagement.entity.ExamSchedule;
import com.hau.ExamInvigilationManagement.entity.Lecturer;
import com.hau.ExamInvigilationManagement.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDetailRepository extends JpaRepository<PaymentDetail, Long> {
    boolean existsByPayment_LecturerAndExamSchedule(
            Lecturer lecturer,
            ExamSchedule examSchedule
    );
}