package com.hau.ExamInvigilationManagement.service.impl;

import com.hau.ExamInvigilationManagement.dto.request.DepartmentRequest;
import com.hau.ExamInvigilationManagement.dto.response.DepartmentResponse;
import com.hau.ExamInvigilationManagement.entity.Department;
import com.hau.ExamInvigilationManagement.exception.AppException;
import com.hau.ExamInvigilationManagement.exception.ErrorCode;
import com.hau.ExamInvigilationManagement.mapper.DepartmentMapper;
import com.hau.ExamInvigilationManagement.repository.DepartmentRepository;
import com.hau.ExamInvigilationManagement.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repository;
    private final DepartmentMapper mapper;

    @Override
    public DepartmentResponse create(DepartmentRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.DEPARTMENT_EXISTED);
        }

        Department department = mapper.toEntity(request);
        return mapper.toResponse(repository.save(department));
    }

    @Override
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));

        mapper.updateEntity(department, request);
        return mapper.toResponse(repository.save(department));
    }

    @Override
    public void delete(Long id) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));

        repository.delete(department);
    }

    @Override
    public DepartmentResponse getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.DEPARTMENT_NOT_FOUND));
    }

    @Override
    public Page<DepartmentResponse> getAllWithPagination(Pageable pageable) {
        Page<Department> page = repository.findAll(pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    public Page<DepartmentResponse> searchByKeyword(String keyword, Pageable pageable) {
        Page<Department> page = repository.searchByKeyword(keyword, pageable);
        return page.map(mapper::toResponse);
    }

    @Override
    public List<DepartmentResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
