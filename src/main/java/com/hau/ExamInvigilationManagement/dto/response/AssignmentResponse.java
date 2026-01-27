package com.hau.ExamInvigilationManagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentResponse {
    private Long id;
    private Long lecturerId;
    private String lecturerName;
    private String email;
    private String room;
}