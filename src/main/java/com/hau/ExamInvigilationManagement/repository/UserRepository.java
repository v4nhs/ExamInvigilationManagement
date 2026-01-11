package com.hau.ExamInvigilationManagement.repository;

import com.hau.ExamInvigilationManagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<User> searchByKeyword(String keyword, Pageable pageable);

}
