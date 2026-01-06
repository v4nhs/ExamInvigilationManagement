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
    private String examDay;
    private LocalTime examTime;
    private LocalTime endTime;
    private ExamType examType;
    private String room;
    private int studentCount;
    private int invigilatorCount;


    public static ExamScheduleResponse from(ExamSchedule e) {
        return ExamScheduleResponse.builder()
                .id(e.getId())
                .examDate(e.getExamDate())
                .examTime(e.getExamTime())
                .examTime(e.getEndTime())
                .examType(e.getExamType())
                .room(e.getRoom())
                .studentCount(e.getStudentCount())
                .invigilatorCount(e.getInvigilatorCount())
                .build();
    }
}
