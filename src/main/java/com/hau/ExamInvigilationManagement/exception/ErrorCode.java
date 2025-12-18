package com.hau.ExamInvigilationManagement.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNAUTHORIZED(9999, "Uncategorized Exception"),
    KEY_INVALID(1001,"Invalid message key"),
    USER_EXISTED(1002, "User existed"),
    USERNAME_INVALID(1003,"Username must be between 4 and 50 characters"),
    PASSWORD_INVALID(1004,"Password must be at least 6 characters"),
    USER_NOT_EXISTED(1005, "User not existed"),
    UNAUTHENTICATED(1006, "Unauthenticated"),
    ROLE_NOT_FOUND(1007, "Role not found"),
    USER_NOT_FOUND(1008, "User not found"),
    ;

    private int code;
    private String message;
}
