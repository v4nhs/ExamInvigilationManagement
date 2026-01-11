package com.hau.ExamInvigilationManagement.repository;

import com.hau.ExamInvigilationManagement.entity.ExamSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {
    Page<ExamSchedule> findAll(Pageable pageable);
    @Query("SELECT e FROM ExamSchedule e WHERE " +
            "LOWER(e.course.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(e.course.code) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(e.room) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<ExamSchedule> searchByKeyword(String keyword, Pageable pageable);
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
                  "FROM ExamSchedule e WHERE e.course.id = ?1 AND e.examDate = ?2 AND e.examTime = ?3")
    boolean existsByCourseDateAndTime(Long courseId, LocalDate examDate, LocalTime examTime);
}

