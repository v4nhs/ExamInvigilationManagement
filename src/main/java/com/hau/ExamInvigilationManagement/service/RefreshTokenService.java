package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.entity.RefreshToken;
import com.hau.ExamInvigilationManagement.entity.User; // Nhớ import User
import com.hau.ExamInvigilationManagement.repository.RefreshTokenRepository;
import com.hau.ExamInvigilationManagement.repository.UserRepository;
import com.hau.ExamInvigilationManagement.exception.AppException;
import com.hau.ExamInvigilationManagement.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Nhớ import Transactional

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    // Thời gian hết hạn Refresh Token (ví dụ: 7 ngày)
    // 604800000 ms = 7 ngày
    private final long refreshTokenDurationMs = 604800000L;

    @Transactional // QUAN TRỌNG: Đảm bảo tính toàn vẹn dữ liệu khi xóa và thêm mới
    public RefreshToken createRefreshToken(String username) {
        // 1. Lấy thông tin User
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found !"));

        // 2. [FIX LỖI] Kiểm tra xem user này đã có token cũ trong DB chưa
        // Bạn cần đảm bảo RefreshTokenRepository có hàm findByUser(User user)
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);

        // 3. Nếu tồn tại token cũ -> Xóa nó đi để tránh lỗi Duplicate entry
        if (existingToken.isPresent()) {
            refreshTokenRepository.delete(existingToken.get());
            refreshTokenRepository.flush(); // Bắt buộc: Đẩy lệnh xóa xuống DB ngay lập tức
        }

        // 4. Tạo token mới
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}