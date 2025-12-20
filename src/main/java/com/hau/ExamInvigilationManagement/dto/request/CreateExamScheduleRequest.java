package com.hau.ExamInvigilationManagement.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateExamScheduleRequest {
    private Long courseId;
    private String examDay;
    private LocalDate examDate;
    private LocalTime examTime;
    private int invigilatorCount;
}