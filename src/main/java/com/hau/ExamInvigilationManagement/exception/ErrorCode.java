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
    DEPARTMENT_NOT_FOUND(2001, "Department not found"),
    DEPARTMENT_EXISTED(2002, "Department existed"),
    COURSE_NOT_FOUND(3001, "Course not found"),
    LECTURER_NOT_FOUND(4001, "Lecture not found"),
    LECTURER_TIME_CONFLICT(4002, "Lecture time conflict"),
    LECTURER_CONFLICT(4003, "Lecture conflict"),
    INVALID_INVIGILATOR_COUNT(4004, "Invalid invigilator conflict"),
    LECTURER_ALREADY_ASSIGNED(4005, "Lecture already assigned"),
    PAYMENT_NOT_FOUND(5001, "Payment not found"),
    PAYMENT_ERROR(5002, "Payment error"),
    EXAM_CONFLICT(6001, "Exam conflict"),
    EXAM_NOT_FOUND(6002, "Exam not found"),
    SCHEDULE_CONFLICT(7001, "Schedule conflict"),
    ASSIGNMENT_NOT_FOUND(8001, "Assignment not found")
    ;

    private int code;
    private String message;
}
