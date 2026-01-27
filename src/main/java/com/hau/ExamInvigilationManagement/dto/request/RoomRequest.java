package com.hau.ExamInvigilationManagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.hau.ExamInvigilationManagement.entity.RoomStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {
    private String roomName;
    private RoomStatus roomStatus;
}