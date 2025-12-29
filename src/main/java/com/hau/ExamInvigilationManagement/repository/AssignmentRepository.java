package com.hau.ExamInvigilationManagement.repository;

import com.hau.ExamInvigilationManagement.entity.Assignment;
import com.hau.ExamInvigilationManagement.entity.ExamSchedule;
import com.hau.ExamInvigilationManagement.entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    boolean existsByLecturerAndExamSchedule_ExamDateAndExamSchedule_ExamTime(
            Lecturer lecturer,
            LocalDate examDate,
            LocalTime examTime
    );

    List<Assignment> findByExamSchedule(ExamSchedule examSchedule);
}
