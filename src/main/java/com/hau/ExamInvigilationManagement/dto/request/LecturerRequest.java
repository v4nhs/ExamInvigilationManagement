package com.hau.ExamInvigilationManagement.dto.request;

import lombok.Data;

@Data
public class LecturerRequest {
    private String userId;
    private String fullName;
    private Long departmentId;
    private String academicTitle;
    private String specialization;
}