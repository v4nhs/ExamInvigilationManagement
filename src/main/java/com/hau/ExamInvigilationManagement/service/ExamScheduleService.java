package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.dto.request.CreateExamScheduleRequest;
import com.hau.ExamInvigilationManagement.dto.response.ExamScheduleResponse;
import com.hau.ExamInvigilationManagement.dto.response.LecturerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExamScheduleService {
    ExamScheduleResponse create(CreateExamScheduleRequest request);
    List<ExamScheduleResponse> getAll();

    void assignWrittenExam(Long examId, List<Long> lecturerIds, String room, Integer studentCount);
    void assignNonWrittenExam(Long examId, List<Long> lecturerIds, String room, Integer studentCount);   List<LecturerResponse> getAvailableLecturers(Long examScheduleId);
    void unassignLecturer(Long examScheduleId, Long lecturerId);
    void importExamSchedule(MultipartFile file);
    ExamScheduleResponse getById(Long id);
    List<Long> getAssignedLecturerIds(Long examScheduleId);
}
