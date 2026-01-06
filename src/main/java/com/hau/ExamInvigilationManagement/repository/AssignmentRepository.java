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
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByExamSchedule(ExamSchedule examSchedule);

    long countByExamSchedule(ExamSchedule examSchedule);
    boolean existsByExamScheduleAndLecturer(ExamSchedule examSchedule, Lecturer lecturer);
    Optional<Assignment> findByExamScheduleAndLecturer(ExamSchedule examSchedule, Lecturer lecturer);
    @Query("""
        SELECT COUNT(a) FROM Assignment a
        JOIN a.examSchedule e
        WHERE a.lecturer = :lecturer
        AND e.examDate = :date
        AND e.id != :currentExamId      
        AND (
             e.examTime < :newEnd        
             AND 
             e.endTime > :newStart       
        )
    """)
    long countTimeOverlaps(
            @Param("lecturer") Lecturer lecturer,
            @Param("date") LocalDate date,
            @Param("newStart") LocalTime newStart,
            @Param("newEnd") LocalTime newEnd,
            @Param("currentExamId") Long currentExamId
    );
}
