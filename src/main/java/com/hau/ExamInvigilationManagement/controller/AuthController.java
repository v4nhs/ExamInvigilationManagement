package com.hau.ExamInvigilationManagement.controller;

import com.hau.ExamInvigilationManagement.dto.request.AuthRequest;
import com.hau.ExamInvigilationManagement.dto.request.ChangePasswordRequest;
import com.hau.ExamInvigilationManagement.dto.request.TokenRefreshRequest;
import com.hau.ExamInvigilationManagement.dto.response.AuthResponse;
import com.hau.ExamInvigilationManagement.dto.response.ApiResponse;
import com.hau.ExamInvigilationManagement.entity.RefreshToken;
import com.hau.ExamInvigilationManagement.entity.User;
import com.hau.ExamInvigilationManagement.exception.AppException;
import com.hau.ExamInvigilationManagement.exception.ErrorCode;
import com.hau.ExamInvigilationManagement.repository.UserRepository;
import com.hau.ExamInvigilationManagement.security.JwtUtil;
import com.hau.ExamInvigilationManagement.service.AuthService;
import com.hau.ExamInvigilationManagement.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/authenticate")
    public ApiResponse<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticate(request);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getUsername());
        response.setRefreshToken(refreshToken.getToken());
        return ApiResponse.success(response);
    }

    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(@RequestBody ChangePasswordRequest request) {
        // 1. Lấy user hiện tại từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Verify mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID, "Mật khẩu cũ không chính xác");
        }

        // 3. Validate mật khẩu mới
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            throw new AppException(ErrorCode.PASSWORD_INVALID, "Mật khẩu mới không được để trống");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID, "Mật khẩu xác nhận không khớp");
        }

        if (request.getNewPassword().length() < 6) {
            throw new AppException(ErrorCode.PASSWORD_INVALID, "Mật khẩu phải tối thiểu 6 ký tự");
        }

        // 4. Cập nhật mật khẩu
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ApiResponse.success("Đổi mật khẩu thành công");
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(token -> refreshTokenService.verifyExpiration(token))
                .map(token -> {
                    User user = token.getUser();
                    String accessToken = jwtUtil.generateToken(user);
                    return ApiResponse.success(AuthResponse.builder()
                            .token(accessToken)
                            .refreshToken(requestRefreshToken)
                            .authenticated(true)
                            .build());
                })
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED, "Refresh token hết hạn"));
    }
}