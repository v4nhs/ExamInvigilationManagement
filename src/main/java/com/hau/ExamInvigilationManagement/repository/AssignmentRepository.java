package com.hau.ExamInvigilationManagement.repository;

import com.hau.ExamInvigilationManagement.entity.Assignment;
import com.hau.ExamInvigilationManagement.entity.ExamSchedule;
import com.hau.ExamInvigilationManagement.entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Query("""
        SELECT COUNT(a) > 0
        FROM Assignment a
        WHERE a.lecturer = :lecturer
          AND a.examSchedule.examDate = :examDate
          AND a.examSchedule.examTime = :examTime
          AND a.examSchedule.id <> :examScheduleId
    """)
    boolean existsConflict(
            @Param("lecturer") Lecturer lecturer,
            @Param("examDate") LocalDate examDate,
            @Param("examTime") LocalTime examTime,
            @Param("examScheduleId") Long examScheduleId
    );

    long countByExamSchedule(ExamSchedule examSchedule);
}
