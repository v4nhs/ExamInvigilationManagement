package com.hau.ExamInvigilationManagement.service;

import com.hau.ExamInvigilationManagement.entity.Assignment;
import com.hau.ExamInvigilationManagement.entity.ExamSchedule;
import com.hau.ExamInvigilationManagement.entity.Lecturer;

import java.util.List;

public interface AssignmentService {

    void assignLecturers(Long examScheduleId, List<Long> lecturerIds);
}