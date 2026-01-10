package com.hau.ExamInvigilationManagement.dto.response;

import com.hau.ExamInvigilationManagement.entity.RefreshToken;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {
    String token;
    boolean authenticated;
    private String refreshToken;
}
