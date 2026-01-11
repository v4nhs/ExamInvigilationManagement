package com.hau.ExamInvigilationManagement.service.impl;

import com.hau.ExamInvigilationManagement.dto.request.LecturerRequest;
import com.hau.ExamInvigilationManagement.dto.response.LecturerResponse;
import com.hau.ExamInvigilationManagement.entity.Department;
import com.hau.ExamInvigilationManagement.entity.ExamSchedule;
import com.hau.ExamInvigilationManagement.entity.Lecturer;
import com.hau.ExamInvigilationManagement.entity.User;
import com.hau.ExamInvigilationManagement.exception.AppException;
import com.hau.ExamInvigilationManagement.exception.ErrorCode;
import com.hau.ExamInvigilationManagement.mapper.LecturerMapper;
import com.hau.ExamInvigilationManagement.repository.DepartmentRepository;
import com.hau.ExamInvigilationManagement.repository.ExamScheduleRepository;
import com.hau.ExamInvigilationManagement.repository.LecturerRepository;
import com.hau.ExamInvigilationManagement.repository.UserRepository;
import com.hau.ExamInvigilationManagement.service.LecturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LecturerServiceImpl implements LecturerService {

    private final LecturerRepository lecturerRepository;
    private final DepartmentRepository departmentRepo;
    private final LecturerMapper lecturerMapper;
    private final ExamScheduleRepository examScheduleRepository;
    private final UserRepository userRepository;

    @Override
    public LecturerResponse create(LecturerRequest request) {
        lecturerRepository.findByUserId(request.getUserId()).ifPresent(existing -> {
            throw new AppException("Người dùng này đã được thêm làm giảng viên!", ErrorCode.LECTURER_ALREADY_EXISTS);
        });
        User user = userRepository.findById(request.getUserId().toString())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Department dept = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));

        Lecturer lecturer = Lecturer.builder()
                .user(user)
                .fullName(request.getFullName())
                .academicTitle(request.getAcademicTitle())
                .specialization(request.getSpecialization())
                .department(dept)
                .build();

        return lecturerMapper.toResponse(lecturerRepository.save(lecturer));
    }

    @Override
    public LecturerResponse update(Long id, LecturerRequest request) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND));

        Department dept = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));

        lecturer.setFullName(request.getFullName());
        lecturer.setAcademicTitle(request.getAcademicTitle());
        lecturer.setSpecialization(request.getSpecialization());
        lecturer.setDepartment(dept);

        return lecturerMapper.toResponse(lecturerRepository.save(lecturer));
    }
    @Override
    public Page<LecturerResponse> getAllWithPagination(Pageable pageable) {
        Page<Lecturer> page = lecturerRepository.findAll(pageable);
        return page.map(lecturerMapper::toResponse);
    }

    @Override
    public Page<LecturerResponse> searchByKeyword(String keyword, Pageable pageable) {
        Page<Lecturer> page = lecturerRepository.searchByKeyword(keyword, pageable);
        return page.map(lecturerMapper::toResponse);
    }
    @Override
    public List<LecturerResponse> getAll() {
        return lecturerRepository.findAll()
                .stream()
                .map(lecturerMapper::toResponse)
                .toList();
    }

    @Override
    public LecturerResponse getById(Long id) {
        return lecturerMapper.toResponse(
                lecturerRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND))
        );
    }

    @Override
    public void delete(Long id) {
        lecturerRepository.deleteById(id);
    }

    @Override
    public List<LecturerResponse> getAvailableLecturers(Long examScheduleId) {

        ExamSchedule exam = examScheduleRepository.findById(examScheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));

        return lecturerRepository.findAvailableLecturers(
                        exam.getExamDate(),
                        exam.getExamTime()
                )
                .stream()
                .map(lecturerMapper::toResponse)
                .toList();
    }
}
