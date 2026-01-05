package com.hau.ExamInvigilationManagement.repository;

import com.hau.ExamInvigilationManagement.entity.ExamSchedule;
import com.hau.ExamInvigilationManagement.entity.Lecturer;
import com.hau.ExamInvigilationManagement.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentDetailRepository extends JpaRepository<PaymentDetail, Long> {
    @Query("SELECT pd FROM PaymentDetail pd WHERE pd.examSchedule = :exam AND pd.payment.lecturer = :lecturer")
    Optional<PaymentDetail> findByExamAndLecturer(
            @Param("exam") ExamSchedule exam,
            @Param("lecturer") Lecturer lecturer
    );
}