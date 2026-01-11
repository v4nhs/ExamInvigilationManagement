package com.hau.ExamInvigilationManagement.repository;

import com.hau.ExamInvigilationManagement.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByCode(String code);

    boolean existsByCode(String code);
    Page<Department> findAll(Pageable pageable);

    @Query("SELECT d FROM Department d WHERE " +
            "LOWER(d.code) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(d.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Department> searchByKeyword(String keyword, Pageable pageable);

}
