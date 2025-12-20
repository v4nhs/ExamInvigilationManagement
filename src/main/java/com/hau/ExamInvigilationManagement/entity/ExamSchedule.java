package com.hau.ExamInvigilationManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "exam_schedules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String examDay;          // Thứ
    private LocalDate examDate;      // Ngày thi
    private LocalTime examTime;      // Giờ thi

    private int invigilatorCount;    // Số cán bộ coi thi
}
