package com.hau.ExamInvigilationManagement.controller;

import java.util.List;

import com.hau.ExamInvigilationManagement.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.hau.ExamInvigilationManagement.dto.request.RoomRequest;
import com.hau.ExamInvigilationManagement.dto.response.RoomResponse;
import com.hau.ExamInvigilationManagement.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT', 'LECTURER')")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAll() {
        try {
            List<RoomResponse> rooms = roomService.getAll();
            return ResponseEntity.ok(new ApiResponse<>(0, "Success", rooms));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(9999, "Error: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPARTMENT')")
    public ResponseEntity<ApiResponse<RoomResponse>> getById(@PathVariable Long id) {
        try {
            RoomResponse room = roomService.getById(id);
            return ResponseEntity.ok(new ApiResponse<>(0, "Success", room));
        } catch (IllegalArgumentException e) {
            if ("ROOM_NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, "Room not found", null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> create(@RequestBody RoomRequest request) {
        try {
            RoomResponse room = roomService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(0, "Room created successfully", room));
        } catch (IllegalArgumentException e) {
            if ("ROOM_NAME_ALREADY_EXISTS".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(409, "Room name already exists", null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomResponse>> update(@PathVariable Long id, @RequestBody RoomRequest request) {
        try {
            RoomResponse room = roomService.update(id, request);
            return ResponseEntity.ok(new ApiResponse<>(0, "Room updated successfully", room));
        } catch (IllegalArgumentException e) {
            if ("ROOM_NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, "Room not found", null));
            }
            if ("ROOM_NAME_ALREADY_EXISTS".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(409, "Room name already exists", null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            roomService.delete(id);
            return ResponseEntity.ok(new ApiResponse<>(0, "Room deleted successfully", null));
        } catch (IllegalArgumentException e) {
            if ("ROOM_NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, "Room not found", null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}