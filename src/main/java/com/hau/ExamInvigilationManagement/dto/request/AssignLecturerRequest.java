package com.hau.ExamInvigilationManagement.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignLecturerRequest {
    private List<Long> lecturerIds;
    private String room;
    private Integer studentCount;
}
