package com.hau.ExamInvigilationManagement.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "exam_assignments")
public class ExamAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;

    @ManyToOne
    @JoinColumn(name = "exam_schedule_id")
    private ExamSchedule examSchedule;

    @Column(name = "room")
    private String room;

    @Column(name = "student_count")
    private Integer studentCount;

    @Column(name = "assignment_type")
    private String assignmentType;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
}