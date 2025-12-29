package com.hau.ExamInvigilationManagement.dto.response;

import com.hau.ExamInvigilationManagement.entity.ExamType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDetailResponse {
    private Long examScheduleId;
    private ExamType examType;
    private long studentCount;
    private long unitPrice;
    private long amount;
}