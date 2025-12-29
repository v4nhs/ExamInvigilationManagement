package com.hau.ExamInvigilationManagement.dto.request;

import com.hau.ExamInvigilationManagement.entity.ExamType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateExamScheduleRequest {
    private Long courseId;

    private LocalDate examDate;

    @Schema(
            type = "string",
            example = "08:30",
            description = "Giờ thi (HH:mm)"
    )
    private String examTime;

    private String examDay;

    private ExamType examType;

    private Integer studentCount;

    private Integer invigilatorCount;
}