package com.hau.ExamInvigilationManagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.hau.ExamInvigilationManagement.entity.RoomStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String roomName;
    private RoomStatus roomStatus;
}