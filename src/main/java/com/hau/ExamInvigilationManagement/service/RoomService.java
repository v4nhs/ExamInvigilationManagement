package com.hau.ExamInvigilationManagement.service;

import java.util.List;
import com.hau.ExamInvigilationManagement.dto.request.RoomRequest;
import com.hau.ExamInvigilationManagement.dto.response.RoomResponse;

public interface RoomService {
    List<RoomResponse> getAll();
    RoomResponse getById(Long id);
    RoomResponse create(RoomRequest request);
    RoomResponse update(Long id, RoomRequest request);
    void delete(Long id);
}