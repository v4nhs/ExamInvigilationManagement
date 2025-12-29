package com.hau.ExamInvigilationManagement.dto.response;

import com.hau.ExamInvigilationManagement.entity.ExamSchedule;
import com.hau.ExamInvigilationManagement.entity.ExamType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class ExamScheduleResponse {

    private Long id;
    private LocalDate examDate;
    private LocalTime examTime;
    private ExamType examType;
    private int studentCount;
    private int invigilatorCount;

    public static ExamScheduleResponse from(ExamSchedule e) {
        return ExamScheduleResponse.builder()
                .id(e.getId())
                .examDate(e.getExamDate())
                .examTime(e.getExamTime())
                .examType(e.getExamType())
                .studentCount(e.getStudentCount())
                .invigilatorCount(e.getInvigilatorCount())
                .build();
    }
}
