package com.hau.ExamInvigilationManagement.service.impl;

import com.hau.ExamInvigilationManagement.dto.request.AuthRequest;
import com.hau.ExamInvigilationManagement.dto.response.AuthResponse;
import com.hau.ExamInvigilationManagement.entity.User;
import com.hau.ExamInvigilationManagement.exception.AppException;
import com.hau.ExamInvigilationManagement.exception.ErrorCode;
import com.hau.ExamInvigilationManagement.repository.UserRepository;
import com.hau.ExamInvigilationManagement.security.JwtUtil;
import com.hau.ExamInvigilationManagement.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtUtil jwtUtil;

    @Override
    public AuthResponse authenticate(AuthRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        System.out.println("=== DEBUG LOGIN ===");
        System.out.println("1. Username gửi lên: " + request.getUsername());
        System.out.println("2. Password gửi lên: " + request.getPassword());
        System.out.println("3. Password Hash trong DB: " + user.getPassword());
        boolean match = passwordEncoder.matches(request.getPassword(), user.getPassword());
        System.out.println("4. Kết quả so sánh (Match): " + match);
        System.out.println("===================");
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .authenticated(true)
                .token(token)
                .build();
    }
}
