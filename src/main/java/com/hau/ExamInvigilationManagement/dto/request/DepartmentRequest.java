package com.hau.ExamInvigilationManagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentRequest {

    @NotBlank(message = "DEPARTMENT_CODE_REQUIRED")
    private String code;

    @NotBlank(message = "DEPARTMENT_NAME_REQUIRED")
    private String name;
}
