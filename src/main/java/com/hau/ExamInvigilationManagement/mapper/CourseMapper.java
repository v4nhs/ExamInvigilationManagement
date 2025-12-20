package com.hau.ExamInvigilationManagement.mapper;

import com.hau.ExamInvigilationManagement.dto.response.CourseResponse;
import com.hau.ExamInvigilationManagement.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "departmentName", source = "department.name")
    CourseResponse toResponse(Course course);
}

