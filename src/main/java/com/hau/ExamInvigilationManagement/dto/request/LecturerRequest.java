package com.hau.ExamInvigilationManagement.dto.request;

import lombok.Data;

@Data
public class LecturerRequest {
    private String code;
    private String fullName;
    private String email;
    private String phone;
    private Long departmentId;
    private String academicTitle;
    private String specialization;
}