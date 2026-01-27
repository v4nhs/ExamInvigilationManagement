package com.hau.ExamInvigilationManagement.entity;

import lombok.Getter;

@Getter
public enum RoomStatus {
    AVAILABLE("Sẵn sàng"),
    UNAVAILABLE("Không sẵn sàng"),
    MAINTENANCE("Bảo trì");

    private final String displayName;

    RoomStatus(String displayName) {
        this.displayName = displayName;
    }
}