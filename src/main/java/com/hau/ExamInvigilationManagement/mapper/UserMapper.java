package com.hau.ExamInvigilationManagement.mapper;

import com.hau.ExamInvigilationManagement.dto.request.UserCreationRequest;
import com.hau.ExamInvigilationManagement.dto.request.UserUpdateRequest;
import com.hau.ExamInvigilationManagement.dto.response.UserResponse;
import com.hau.ExamInvigilationManagement.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest userCreationRequest);

    @Mapping(target = "email", source = "email")
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest userUpdateRequest);
}