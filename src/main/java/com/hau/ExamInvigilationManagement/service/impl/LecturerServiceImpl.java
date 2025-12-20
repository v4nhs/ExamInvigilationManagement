package com.hau.ExamInvigilationManagement.service.impl;

import com.hau.ExamInvigilationManagement.dto.request.LecturerRequest;
import com.hau.ExamInvigilationManagement.dto.response.LecturerResponse;
import com.hau.ExamInvigilationManagement.entity.Department;
import com.hau.ExamInvigilationManagement.entity.Lecturer;
import com.hau.ExamInvigilationManagement.exception.AppException;
import com.hau.ExamInvigilationManagement.exception.ErrorCode;
import com.hau.ExamInvigilationManagement.mapper.LecturerMapper;
import com.hau.ExamInvigilationManagement.repository.DepartmentRepository;
import com.hau.ExamInvigilationManagement.repository.LecturerRepository;
import com.hau.ExamInvigilationManagement.service.LecturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LecturerServiceImpl implements LecturerService {

    private final LecturerRepository lecturerRepo;
    private final DepartmentRepository departmentRepo;
    private final LecturerMapper lecturerMapper;

    @Override
    public LecturerResponse create(LecturerRequest request) {
        Department dept = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));

        Lecturer lecturer = Lecturer.builder()
                .code(request.getCode())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .department(dept)
                .build();

        return lecturerMapper.toResponse(lecturerRepo.save(lecturer));
    }

    @Override
    public List<LecturerResponse> getAll() {
        return lecturerRepo.findAll()
                .stream()
                .map(lecturerMapper::toResponse)
                .toList();
    }

    @Override
    public LecturerResponse getById(Long id) {
        return lecturerMapper.toResponse(
                lecturerRepo.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND))
        );
    }

    @Override
    public LecturerResponse update(Long id, LecturerRequest request) {
        Lecturer lecturer = lecturerRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LECTURER_NOT_FOUND));

        Department dept = departmentRepo.findById(request.getDepartmentId())
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));

        lecturer.setCode(request.getCode());
        lecturer.setFullName(request.getFullName());
        lecturer.setEmail(request.getEmail());
        lecturer.setPhone(request.getPhone());
        lecturer.setDepartment(dept);

        return lecturerMapper.toResponse(lecturerRepo.save(lecturer));
    }

    @Override
    public void delete(Long id) {
        lecturerRepo.deleteById(id);
    }
}
