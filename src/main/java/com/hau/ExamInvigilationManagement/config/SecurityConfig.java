package com.hau.ExamInvigilationManagement.config;

import com.hau.ExamInvigilationManagement.security.JwtAuthenticationFilter;
import org.springframework.http.HttpMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/swagger-resources",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()
                        // ===== PUBLIC ENDPOINTS =====
                        .requestMatchers("/api/auth/**").permitAll()

                        // ===== USER MANAGEMENT (ADMIN ONLY) =====
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasAuthority("ROLE_ADMIN")

                        // ===== ROLE MANAGEMENT (ADMIN ONLY) =====
                        .requestMatchers(HttpMethod.GET, "/api/roles").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/roles/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/roles").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/roles/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/roles/*").hasAuthority("ROLE_ADMIN")

                                // ===== DEPARTMENT MANAGEMENT =====
                        // VIEW: ADMIN, DEPARTMENT, ACCOUNTING
                        .requestMatchers(HttpMethod.GET, "/api/departments").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEPARTMENT", "ROLE_ACCOUNTING")
                        .requestMatchers(HttpMethod.GET, "/api/departments/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEPARTMENT", "ROLE_ACCOUNTING")
                        // CREATE/UPDATE/DELETE: ADMIN ONLY
                        .requestMatchers(HttpMethod.POST, "/api/departments").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/departments/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/departments/*").hasAuthority("ROLE_ADMIN")
                        // ===== COURSE MANAGEMENT =====
                        // VIEW: ADMIN, DEPARTMENT, ACCOUNTING
                        .requestMatchers(HttpMethod.GET, "/api/courses").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEPARTMENT", "ROLE_ACCOUNTING")
                        .requestMatchers(HttpMethod.GET, "/api/courses/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEPARTMENT", "ROLE_ACCOUNTING")
                        // CREATE/UPDATE/DELETE: ADMIN ONLY
                        .requestMatchers(HttpMethod.POST, "/api/courses").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/courses/import").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/*").hasAuthority("ROLE_ADMIN")

                        // ===== LECTURER MANAGEMENT =====
                        // VIEW: ADMIN, DEPARTMENT, ACCOUNTING
                        .requestMatchers(HttpMethod.GET, "/api/lecturers").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEPARTMENT", "ROLE_ACCOUNTING")
                        .requestMatchers(HttpMethod.GET, "/api/lecturers/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEPARTMENT", "ROLE_ACCOUNTING")
                        // MANAGE: ADMIN, DEPARTMENT (can manage lecturers)
                        .requestMatchers(HttpMethod.POST, "/api/lecturers").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEPARTMENT")
                        .requestMatchers(HttpMethod.PUT, "/api/lecturers/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEPARTMENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/lecturers/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEPARTMENT")

                        // ===== EXAM SCHEDULES =====
                        // VIEW: ADMIN, DEPARTMENT, ACCOUNTING
                        .requestMatchers(HttpMethod.GET, "/api/exam-schedules").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEPARTMENT", "ROLE_ACCOUNTING")
                        .requestMatchers(HttpMethod.GET, "/api/exam-schedules/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEPARTMENT", "ROLE_ACCOUNTING")
                        // MANAGE: ADMIN ONLY
                        .requestMatchers(HttpMethod.POST, "/api/exam-schedules").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/exam-schedules/import").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/exam-schedules/*").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/exam-schedules/*").hasAuthority("ROLE_ADMIN")

                        // ===== EXAM ASSIGNMENTS - LECTURER ASSIGNMENT =====
                        // DEPARTMENT can manage assignments
                        .requestMatchers(HttpMethod.GET, "/api/exam-schedules/*/available-lecturers").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEPARTMENT")
                        .requestMatchers(HttpMethod.POST, "/api/exam-schedules/*/assign-written").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEPARTMENT")
                        .requestMatchers(HttpMethod.POST, "/api/exam-schedules/*/assign-other").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEPARTMENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/exam-schedules/*/assign/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_DEPARTMENT")

                        // ===== PAYMENT/ACCOUNTING MANAGEMENT =====
                        // ACCOUNTING can view and manage payments
                        .requestMatchers(HttpMethod.GET, "/api/payments").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTING")
                        .requestMatchers(HttpMethod.GET, "/api/payments/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTING")
                        .requestMatchers(HttpMethod.POST, "/api/payments").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTING")
                        .requestMatchers(HttpMethod.PUT, "/api/payments/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTING")
                        .requestMatchers(HttpMethod.DELETE, "/api/payments/*").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTING")
                        .requestMatchers(HttpMethod.GET, "/api/payments/*/settlement").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTING")
                        .requestMatchers(HttpMethod.POST, "/api/payments/*/settlement").hasAnyAuthority("ROLE_ADMIN", "ROLE_ACCOUNTING")

                        // Any other request requires authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
