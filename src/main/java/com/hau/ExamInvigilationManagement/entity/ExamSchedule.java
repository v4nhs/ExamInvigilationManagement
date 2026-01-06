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
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "exam_date")
    private LocalDate examDate;

    @Column(name = "exam_day")
    private String examDay;

    @Column(name = "exam_time")
    private LocalTime examTime;

    // Thêm nullable = true nếu muốn cho phép null lúc đầu
    @Column(name = "end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type")
    private ExamType examType;

    @Column(name = "room", length = 50)
    private String room;

    @Column(name = "student_count")
    private Integer studentCount;

    @Column(name = "invigilator_count")
    private Integer invigilatorCount;
}