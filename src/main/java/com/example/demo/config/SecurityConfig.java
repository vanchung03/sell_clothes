package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .cors()
                .and()
                .authorizeHttpRequests(auth -> auth
                        // Cho phép các request OPTIONS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Các endpoint không cần xác thực
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(ApiPermissions.SHARED_APIS).hasAnyRole("ADMIN", "DEALER", "USER")
                        .requestMatchers(ApiPermissions.ADMIN_APIS).hasRole("ADMIN")
                        .requestMatchers(ApiPermissions.DEALER_APIS).hasRole("DEALER")
                        .requestMatchers(ApiPermissions.USER_APIS).hasRole("USER")
                        .anyRequest().authenticated()
                )
                // Cấu hình xử lý lỗi cho trường hợp không có quyền truy cập
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN))  // Mã lỗi 403 khi không có quyền truy cập
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
