package com.hau.ExamInvigilationManagement.controller;

import com.hau.ExamInvigilationManagement.dto.request.AuthRequest;
import com.hau.ExamInvigilationManagement.dto.request.TokenRefreshRequest;
import com.hau.ExamInvigilationManagement.dto.response.AuthResponse;
import com.hau.ExamInvigilationManagement.entity.RefreshToken;
import com.hau.ExamInvigilationManagement.entity.User;
import com.hau.ExamInvigilationManagement.security.JwtUtil;
import com.hau.ExamInvigilationManagement.service.AuthService;
import com.hau.ExamInvigilationManagement.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/authenticate")
    public AuthResponse authenticate(@RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticate(request);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getUsername());
        response.setRefreshToken(refreshToken.getToken());

        return response;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(token -> refreshTokenService.verifyExpiration(token))
                .map(token -> {
                    User user = token.getUser();
                    String accessToken = jwtUtil.generateToken(user);
                    return ResponseEntity.ok(AuthResponse.builder()
                            .token(accessToken)
                            .refreshToken(requestRefreshToken)
                            .authenticated(true)
                            .build());
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database or expired!"));
    }
}