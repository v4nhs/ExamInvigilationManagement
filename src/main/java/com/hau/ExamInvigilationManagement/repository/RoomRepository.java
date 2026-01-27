package com.hau.ExamInvigilationManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hau.ExamInvigilationManagement.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByRoomName(String roomName);
}