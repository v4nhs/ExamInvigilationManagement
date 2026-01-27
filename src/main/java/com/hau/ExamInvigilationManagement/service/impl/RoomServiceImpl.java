package com.hau.ExamInvigilationManagement.service.impl;

import java.util.List;

import com.hau.ExamInvigilationManagement.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hau.ExamInvigilationManagement.dto.request.RoomRequest;
import com.hau.ExamInvigilationManagement.dto.response.RoomResponse;
import com.hau.ExamInvigilationManagement.entity.Room;
import com.hau.ExamInvigilationManagement.mapper.RoomMapper;
import com.hau.ExamInvigilationManagement.repository.RoomRepository;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomMapper roomMapper;

    @Override
    public List<RoomResponse> getAll() {
        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toRoomResponse)
                .toList();
    }

    @Override
    public RoomResponse getById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ROOM_NOT_FOUND"));
        return roomMapper.toRoomResponse(room);
    }

    @Override
    public RoomResponse create(RoomRequest request) {
        if (roomRepository.existsByRoomName(request.getRoomName())) {
            throw new IllegalArgumentException("ROOM_NAME_ALREADY_EXISTS");
        }
        Room room = roomMapper.toRoom(request);
        Room savedRoom = roomRepository.save(room);
        return roomMapper.toRoomResponse(savedRoom);
    }

    @Override
    public RoomResponse update(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ROOM_NOT_FOUND"));

        if (!room.getRoomName().equals(request.getRoomName()) &&
                roomRepository.existsByRoomName(request.getRoomName())) {
            throw new IllegalArgumentException("ROOM_NAME_ALREADY_EXISTS");
        }

        roomMapper.updateRoom(request, room);
        Room updatedRoom = roomRepository.save(room);
        return roomMapper.toRoomResponse(updatedRoom);
    }

    @Override
    public void delete(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ROOM_NOT_FOUND"));
        roomRepository.delete(room);
    }
}