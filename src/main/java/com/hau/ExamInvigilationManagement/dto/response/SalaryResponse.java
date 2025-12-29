package com.hau.ExamInvigilationManagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SalaryResponse {
    private Long lecturerId;
    private String lecturerName;
    private Long totalAmount;
    private List<PaymentDetailResponse> details;
}