package com.hau.ExamInvigilationManagement.dto.response;

import com.hau.ExamInvigilationManagement.entity.Department;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LecturerResponse {
    private Long id;
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String fullName;
    private Long departmentId;
    private String departmentName;
    private String academicTitle;
    private String specialization;
}
