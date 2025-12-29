package com.hau.ExamInvigilationManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Table(name = "exam_schedules")
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Course course;

    private LocalDate examDate;
    private LocalTime examTime;
    private String examDay;

    @Enumerated(EnumType.STRING)
    private ExamType examType;   // WRITTEN / STUDENT_BASED

    private Integer studentCount;
    private Integer invigilatorCount;
}