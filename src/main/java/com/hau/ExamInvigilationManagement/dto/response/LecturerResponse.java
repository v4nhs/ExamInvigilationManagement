package com.hau.ExamInvigilationManagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LecturerResponse {
    private Long id;
    private String code;
    private String fullName;
    private String email;
    private String phone;
    private String departmentName;
}
