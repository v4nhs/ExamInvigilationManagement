package com.hau.ExamInvigilationManagement.repository;

import com.hau.ExamInvigilationManagement.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByCode(String code);
    Optional<Course> findByCode(String code);
    Page<Course> findAll(Pageable pageable);

    @Query("SELECT c FROM Course c WHERE " +
            "LOWER(c.code) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(c.department.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Course> searchByKeyword(String keyword, Pageable pageable);
}

