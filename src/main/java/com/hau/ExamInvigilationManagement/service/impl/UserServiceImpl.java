package com.hau.ExamInvigilationManagement.service.impl;

import com.hau.ExamInvigilationManagement.dto.request.UserCreationRequest;
import com.hau.ExamInvigilationManagement.dto.request.UserUpdateRequest;
import com.hau.ExamInvigilationManagement.dto.response.UserResponse;
import com.hau.ExamInvigilationManagement.entity.Role;
import com.hau.ExamInvigilationManagement.entity.User;
import com.hau.ExamInvigilationManagement.exception.AppException;
import com.hau.ExamInvigilationManagement.exception.ErrorCode;
import com.hau.ExamInvigilationManagement.mapper.UserMapper;
import com.hau.ExamInvigilationManagement.repository.RoleRepository;
import com.hau.ExamInvigilationManagement.repository.UserRepository;
import com.hau.ExamInvigilationManagement.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserCreationRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        List<Long> roleIds = request.getRoleIds();
        List<Role> roles = roleRepository.findAllById(roleIds);
        if (roles.isEmpty() && !roleIds.isEmpty()) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
        user.setRoles(new HashSet<>(roles));


        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public Page<UserResponse> getAllWithPagination(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return page.map(userMapper::toUserResponse);
    }

    @Override
    public Page<UserResponse> searchByKeyword(String keyword, Pageable pageable) {
        Page<User> page = userRepository.searchByKeyword(keyword, pageable);
        return page.map(userMapper::toUserResponse);
    }

    @Override
    public List<UserResponse> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(String userId, UserUpdateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getRoleIds() != null) {
            List<Role> roles = roleRepository.findAllById(request.getRoleIds());
        if (roles.isEmpty())
            throw new RuntimeException(ErrorCode.ROLE_NOT_FOUND.name());

        user.setRoles(new HashSet<>(roles));
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }
    @Override
    public UserResponse assignRole(String userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        user.getRoles().add(role);
        return userMapper.toUserResponse(userRepository.save(user));
    }



}