package com.hau.ExamInvigilationManagement.dto.response;

import com.hau.ExamInvigilationManagement.entity.Department;
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
    private Department department;
    private String departmentName;
    private String academicTitle;
    private String specialization;
}
