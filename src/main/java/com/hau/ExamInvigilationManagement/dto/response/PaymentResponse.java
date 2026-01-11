package com.hau.ExamInvigilationManagement.dto.response;

import com.hau.ExamInvigilationManagement.entity.Payment;
import com.hau.ExamInvigilationManagement.entity.PaymentStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long lecturerId;
    private String lecturerName;
    private Long totalAmount;
    private PaymentStatus status;
}