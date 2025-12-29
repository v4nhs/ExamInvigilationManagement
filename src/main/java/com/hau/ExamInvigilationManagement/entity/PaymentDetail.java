package com.hau.ExamInvigilationManagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetail {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Payment payment;

    @ManyToOne
    private ExamSchedule examSchedule;

    private Long amount;
}
