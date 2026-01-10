package com.hau.ExamInvigilationManagement.mapper;

import com.hau.ExamInvigilationManagement.dto.response.LecturerResponse;
import com.hau.ExamInvigilationManagement.entity.Lecturer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LecturerMapper {

    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "department", source = "department")
    @Mapping(target = "academicTitle", source = "academicTitle")
    @Mapping(target = "specialization", source = "specialization")
    LecturerResponse toResponse(Lecturer lecturer);
}

