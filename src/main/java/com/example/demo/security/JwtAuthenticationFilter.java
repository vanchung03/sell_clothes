package com.example.demo.security;

import com.example.demo.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Lấy header Authorization từ request
            String authHeader = request.getHeader("Authorization");

            // Kiểm tra nếu header không có hoặc không bắt đầu bằng "Bearer "
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Lấy token JWT từ header
            String jwt = authHeader.substring(7);

            // Trích xuất username từ token
            String username = jwtUtil.extractUsername(jwt);

            // Xác thực username và kiểm tra authentication hiện tại
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Tải thông tin user từ dịch vụ CustomUserDetailsService
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Kiểm tra tính hợp lệ của token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // Tạo đối tượng authentication và set vào SecurityContext
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log lỗi để tiện debugging
            System.err.println("Lỗi trong JwtAuthenticationFilter: " + e.getMessage());
        }

        // Tiếp tục chuỗi filter
        filterChain.doFilter(request, response);
    }
}
