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
    private Long courseId;
    private String courseName;
    private String courseCode;
    private LocalDate examDate;
    private String examDay;
    private LocalTime examTime;
    private LocalTime endTime;
    private ExamType examType;
    private String room;
    private Integer studentCount;
    private Integer invigilatorCount;

    public static ExamScheduleResponse from(ExamSchedule e) {
        return ExamScheduleResponse.builder()
                .id(e.getId())
                .courseId(e.getCourse() != null ? e.getCourse().getId() : null)
                .courseName(e.getCourse() != null ? e.getCourse().getName() : null)
                .courseCode(e.getCourse() != null ? e.getCourse().getCode() : null)
                .examDate(e.getExamDate())
                .examDay(e.getExamDay())
                .examTime(e.getExamTime())
                .endTime(e.getEndTime())
                .examType(e.getExamType())
                .room(e.getRoom())
                .studentCount(e.getStudentCount())
                .invigilatorCount(e.getInvigilatorCount())
                .build();
    }
}