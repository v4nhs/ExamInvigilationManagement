package com.hau.ExamInvigilationManagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class ExamScheduleResponse {
    private Long id;
    private String courseCode;
    private String courseName;
    private String examDay;
    private LocalDate examDate;
    private LocalTime examTime;
    private int invigilatorCount;
}
