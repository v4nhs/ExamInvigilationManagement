package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.dto.request.UserCreationRequest;
import com.hau.ExamInvigilationManagement.dto.request.UserUpdateRequest;
import com.hau.ExamInvigilationManagement.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreationRequest request);

    List<UserResponse> getUsers();

    UserResponse getUserById(String id);

    UserResponse updateUser(String userId, UserUpdateRequest request);

    void deleteUser(String id);

    UserResponse assignRole(String userId, Long roleId);

    Page<UserResponse> getAllWithPagination(Pageable pageable);
    Page<UserResponse> searchByKeyword(String keyword, Pageable pageable);
}
