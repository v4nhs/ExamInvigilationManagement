package com.hau.ExamInvigilationManagement.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseRequest {
    private String code;
    private String name;
    private Long departmentId;
}
