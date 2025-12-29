package com.hau.ExamInvigilationManagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_detail")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PaymentDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "exam_schedule_id")
    private ExamSchedule examSchedule;

    @Enumerated(EnumType.STRING)
    private ExamType examType;

    private Long studentCount;

    private Long unitPrice;

    private Long amount;
}
