package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.dto.request.AuthRequest;
import com.hau.ExamInvigilationManagement.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse authenticate(AuthRequest request);

}
