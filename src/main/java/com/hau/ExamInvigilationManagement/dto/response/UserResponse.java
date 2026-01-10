package com.hau.ExamInvigilationManagement.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.management.relation.Role;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String password;
    String firstName;
    String lastName;
    Set<RoleResponse> roles;
}
