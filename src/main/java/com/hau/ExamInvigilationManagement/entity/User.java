package com.hau.ExamInvigilationManagement.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @NotBlank(message = "Username không được trống")
    private String username;
    @NotBlank(message = "Password không được trống")
    @Size(min = 4, message = "Password phải từ 4 kí tự trở lên")
    private String password;
    @NotBlank(message = "FirstName không được trống")
    private String firstName;
    @NotBlank(message = "LastName không được trống")
    private String lastName;
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được trống")
    private String email;

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private boolean accountNonExpired = true;

    @Builder.Default
    private boolean accountNonLocked = true;

    @Builder.Default
    private boolean credentialsNonExpired = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    // --- SỬA 4 HÀM NÀY: TRẢ VỀ TRUE CỨNG ---

    @Override
    public boolean isAccountNonExpired() {
        return true; // <--- Đừng return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // <--- Đừng return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // <--- Đừng return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return true; // <--- Đừng return this.enabled;
    }
}
