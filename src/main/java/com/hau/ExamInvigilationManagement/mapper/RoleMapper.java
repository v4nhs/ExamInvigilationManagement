package com.hau.ExamInvigilationManagement.mapper;

import com.hau.ExamInvigilationManagement.dto.request.RoleRequest;
import com.hau.ExamInvigilationManagement.dto.response.RoleResponse;
import com.hau.ExamInvigilationManagement.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);
    void updateRole(@MappingTarget Role role, RoleRequest request);
}