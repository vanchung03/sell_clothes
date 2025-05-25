
package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
   @Value("${jwt.secret}")
    private  String jwtSecret ;
    private final long accessTokenExpirationMs = 1000 * 60 * 60;  // 60 phút
    private final long refreshTokenExpirationMs = 86400000; // 1 ngày

    public String generateAccessToken(String username, String email, List<String> roles, String status,String user_id) {
        return Jwts.builder()
                .setSubject(username)
                .claim("email", email)
                .claim("roles", roles)
                .claim("status", status)
                .claim("user_id",user_id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String extractUserID(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new RuntimeException("Token expired", e); // Ném RuntimeException với thông báo
        } catch (io.jsonwebtoken.SignatureException e) {
            throw new RuntimeException("Invalid signature", e); // Ném RuntimeException với thông báo
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            throw new RuntimeException("Malformed token", e); // Ném RuntimeException với thông báo
        } catch (Exception e) {
            throw new RuntimeException("Unknown error occurred while validating token", e); // Ném RuntimeException với thông báo
        }
    }
    // Hàm lấy user từ token
    public String getUserFromToken(String token) {
        try {
            // Xử lý token (xóa "Bearer " nếu có trong header)
            String jwtToken = token.substring(7); // Bỏ "Bearer " nếu có

            // Giải mã JWT và lấy thông tin người dùng
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)  // Secret key để giải mã
                    .parseClaimsJws(jwtToken)  // Giải mã token
                    .getBody();

            // Trả về username từ claims
            return claims.getSubject();  // Lấy subject, thường là username
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }
}
