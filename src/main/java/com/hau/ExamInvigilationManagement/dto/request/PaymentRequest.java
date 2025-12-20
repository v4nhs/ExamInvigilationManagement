package com.hau.ExamInvigilationManagement.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentRequest {
    private Double amount;
    private LocalDate paymentDate;
    private Long lecturerId;
}
