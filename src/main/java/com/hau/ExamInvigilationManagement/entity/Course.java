package com.hau.ExamInvigilationManagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;   // mã HP
    private String name;   // tên HP

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
