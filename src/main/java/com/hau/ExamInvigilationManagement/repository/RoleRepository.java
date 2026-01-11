package com.hau.ExamInvigilationManagement.repository;

import com.hau.ExamInvigilationManagement.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
    Page<Role> findAll(Pageable pageable);

    @Query("SELECT r FROM Role r WHERE " +
            "LOWER(r.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Role> searchByKeyword(String keyword, Pageable pageable);
}
