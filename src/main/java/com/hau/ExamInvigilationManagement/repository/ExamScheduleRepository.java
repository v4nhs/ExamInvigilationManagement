package com.hau.ExamInvigilationManagement.repository;

import com.hau.ExamInvigilationManagement.dto.request.CreateExamScheduleRequest;
import com.hau.ExamInvigilationManagement.dto.response.ExamScheduleResponse;
import com.hau.ExamInvigilationManagement.entity.ExamSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {
//    ExamScheduleResponse create(CreateExamScheduleRequest request);

}

