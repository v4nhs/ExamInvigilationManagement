package com.hau.ExamInvigilationManagement.mapper;

import com.hau.ExamInvigilationManagement.dto.request.DepartmentRequest;
import com.hau.ExamInvigilationManagement.dto.response.DepartmentResponse;
import com.hau.ExamInvigilationManagement.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    Department toEntity(DepartmentRequest request);

    DepartmentResponse toResponse(Department department);

    void updateEntity(@MappingTarget Department department,
                      DepartmentRequest request);
}
