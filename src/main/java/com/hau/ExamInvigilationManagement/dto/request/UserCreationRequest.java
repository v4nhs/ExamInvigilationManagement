package com.hau.ExamInvigilationManagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreationRequest {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private List<Long> roleIds;
}
