package com.hau.ExamInvigilationManagement.config;

import com.hau.ExamInvigilationManagement.entity.Role;
import com.hau.ExamInvigilationManagement.entity.User;
import com.hau.ExamInvigilationManagement.repository.RoleRepository;
import com.hau.ExamInvigilationManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").description("Quản trị viên hệ thống").build()));
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").description("Người dùng cơ bản").build()));
        Role accountingRole = roleRepository.findByName("ROLE_ACCOUNTING")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ACCOUNTING").description("Kế toán").build()));
        Role lecturerRole = roleRepository.findByName("ROLE_LECTURER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_LECTURER").description("Giảng viên").build()));
        Role department = roleRepository.findByName("ROLE_DEPARTMENT")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_DEPARTMENT").description("Khoa").build()));
        User admin = userRepository.findByUsername("admin").orElse(null);

        if (admin == null) {
            admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@example.com")
                    .firstName("System")
                    .lastName("Administrator")
                    .roles(new HashSet<>())
                    .enabled(true)
                    .accountNonLocked(true)
                    .accountNonExpired(true)
                    .credentialsNonExpired(true)
                    .build();

            admin.getRoles().add(adminRole);
            userRepository.save(admin);
            log.info(">>> Đã khởi tạo tài khoản ADMIN mới thành công.");

        } else {
            if (admin.getRoles() == null) {
                admin.setRoles(new HashSet<>());
            }
            boolean hasAdminRole = admin.getRoles().stream()
                    .anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
            if (!hasAdminRole) {
                admin.getRoles().add(adminRole);
                log.warn(">>> Đã bổ sung quyền ADMIN.");
            }
            admin.setPassword(passwordEncoder.encode("admin123"));
            userRepository.save(admin);
            log.info(">>> Đã FORCE UPDATE: Cập nhật quyền và reset mật khẩu admin thành công.");
        }
    }
}