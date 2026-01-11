package com.hau.ExamInvigilationManagement.mapper;

import com.hau.ExamInvigilationManagement.dto.response.LecturerResponse;
import com.hau.ExamInvigilationManagement.entity.Department;
import com.hau.ExamInvigilationManagement.entity.Lecturer;
import com.hau.ExamInvigilationManagement.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface LecturerMapper {

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "fullName", source = "user", qualifiedByName = "userToFullName")
    @Mapping(target = "departmentId", source = "department", qualifiedByName = "departmentToDepartmentId")
    @Mapping(target = "departmentName", source = "department", qualifiedByName = "departmentToDepartmentName")
    LecturerResponse toResponse(Lecturer lecturer);

    @Named("userToFullName")
    default String userToFullName(User user) {
        if (user == null) return null;
        return (user.getFirstName() != null ? user.getFirstName() : "") + " " +
                (user.getLastName() != null ? user.getLastName() : "");
    }

    @Named("departmentToDepartmentId")
    default Long departmentToDepartmentId(Department department) {
        return department != null ? department.getId() : null;
    }

    @Named("departmentToDepartmentName")
    default String departmentToDepartmentName(Department department) {
        return department != null ? department.getName() : null;
    }
}