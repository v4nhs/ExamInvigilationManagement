package com.hau.ExamInvigilationManagement.repository;

import com.hau.ExamInvigilationManagement.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByCode(String code);
}

