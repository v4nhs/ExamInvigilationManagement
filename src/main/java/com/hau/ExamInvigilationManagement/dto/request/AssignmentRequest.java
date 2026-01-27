package com.hau.ExamInvigilationManagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequest {
    private Long examScheduleId;
    private List<Long> lecturerIds;
    private String room;
    private Integer studentCount;
}