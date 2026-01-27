package com.hau.ExamInvigilationManagement.mapper;

import com.hau.ExamInvigilationManagement.dto.request.RoomRequest;
import com.hau.ExamInvigilationManagement.dto.response.RoomResponse;
import com.hau.ExamInvigilationManagement.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    Room toRoom(RoomRequest request);
    RoomResponse toRoomResponse(Room room);
    void updateRoom(RoomRequest request, @MappingTarget Room room);
}
