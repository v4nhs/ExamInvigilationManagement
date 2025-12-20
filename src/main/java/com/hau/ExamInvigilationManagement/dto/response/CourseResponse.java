package com.hau.ExamInvigilationManagement.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CourseResponse {
    private Long id;
    private String code;
    private String name;
    private String departmentName;
}

