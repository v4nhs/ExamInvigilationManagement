package com.hau.ExamInvigilationManagement.mapper;

import com.hau.ExamInvigilationManagement.dto.response.LecturerResponse;
import com.hau.ExamInvigilationManagement.entity.Department;
import com.hau.ExamInvigilationManagement.entity.Lecturer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface LecturerMapper {

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "departmentId", source = "department", qualifiedByName = "departmentToDepartmentId")
    @Mapping(target = "departmentName", source = "department", qualifiedByName = "departmentToDepartmentName")
    LecturerResponse toResponse(Lecturer lecturer);

    @Named("departmentToDepartmentId")
    default Long departmentToDepartmentId(Department department) {
        return department != null ? department.getId() : null;
    }

    @Named("departmentToDepartmentName")
    default String departmentToDepartmentName(Department department) {
        return department != null ? department.getName() : null;
    }
}

