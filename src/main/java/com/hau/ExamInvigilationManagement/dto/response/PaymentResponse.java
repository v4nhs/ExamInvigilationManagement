package com.hau.ExamInvigilationManagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private Double amount;
    private LocalDate paymentDate;
    private String lecturerName;
    private String status;
}
