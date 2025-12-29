package com.hau.ExamInvigilationManagement.repository;

import com.hau.ExamInvigilationManagement.entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

    @Query("""
        SELECT l FROM Lecturer l
        WHERE l.id NOT IN (
            SELECT a.lecturer.id FROM Assignment a
            WHERE a.examSchedule.examDate = :date
              AND a.examSchedule.examTime = :time
        )
    """)
    List<Lecturer> findAvailableLecturers(
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );
}
