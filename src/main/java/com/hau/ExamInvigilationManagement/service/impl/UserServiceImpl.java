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
    PasswordEncoder passwordEncoder; // Đã inject qua constructor (Lombok), không cần new ở đây

    @Override
    public UserResponse createUser(UserCreationRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // --- SỬA ĐỔI: Xử lý nhiều Role ---
        // Lấy danh sách ID từ request
        List<Long> roleIds = request.getRoleIds();

        // Tìm tất cả Role trong DB theo danh sách ID
        List<Role> roles = roleRepository.findAllById(roleIds);

        // (Tuỳ chọn) Kiểm tra nếu không tìm thấy role nào hợp lệ
        if (roles.isEmpty() && !roleIds.isEmpty()) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }

        // Gán HashSet các roles vào user
        user.setRoles(new HashSet<>(roles));
        // --------------------------------

        return userMapper.toUserResponse(userRepository.save(user));
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

        // Cập nhật mật khẩu nếu có
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Cập nhật các thông tin cơ bản
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());

        // --- SỬA ĐỔI: Cập nhật nhiều Role ---
        if (request.getRoleIds() != null) {
            List<Role> roles = roleRepository.findAllById(request.getRoleIds());

            // Nếu muốn bắt buộc phải có role tồn tại thì check empty ở đây
            // if (roles.isEmpty()) throw ...

            user.setRoles(new HashSet<>(roles));
        }
        // ------------------------------------

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