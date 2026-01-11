package com.hau.ExamInvigilationManagement.repository;

import com.hau.ExamInvigilationManagement.entity.ExamAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamAssignmentRepository extends JpaRepository<ExamAssignment, Long> {
    List<ExamAssignment> findByLecturerId(Long lecturerId);
}