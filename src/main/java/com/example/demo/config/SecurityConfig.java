package com.example.demo.config;

import com.example.demo.entity.RoleName;
import com.example.demo.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity httpSecurity, CustomUserDetailsService userDetailsService) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeRequests()
                .requestMatchers("/api/auth/**").permitAll()

                .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/products").hasRole("USER")
                .requestMatchers(
                        HttpMethod.PUT,
                        "/api/v1/products/{id}")
                .hasRole("DEALER")

                .requestMatchers(
                        HttpMethod.DELETE,
                        "/api/v1/products/{id}")
                .hasRole("DEALER")


                .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/products",
                        "/api/v1/brands",
                        "/api/v1/categories",
                        "/api/v1/product_images/**",
                        "/api/v1/product_variants/**",
                        "/api/v1/products/**"
                ).permitAll();
//                .anyRequest().permitAll();

        return httpSecurity.build();
    }
}
