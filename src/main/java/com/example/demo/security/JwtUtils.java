
package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
    public class JwtUtils {

        private final String jwtSecret = "6d5775091ee7118aa63cf885fbde1924c05fa6ae19f8434c1f2efac1978fd4c211727daea62eff3c3afed314d625f6c466531efe6b833fd0100e9689a0e221f0ac213e42c229f4bb3caf5c05e2bd28b5e500c8b64addd2932c2e59d64cc3dc9e1815c036c5446784bfe27c066bcf58f5ec0b9c31ce212eace1a69593779730a365ee7ff844a2789073744c130ac3e86c1d7ccb713b82b9047dedb53bf29e4de4d52084780f3e8a2aeae51f18daba8f87a2079939331eb6f8e48c1610cb3f7c5bf3870fe69665b0a06888359b3604bb5e449297cc9686d51944398db9cd80eeb178e0e3f196368eecb298c48cf97e7cb28bbf9c9d7b9da0ed424b523dd2e3ec92";
        private final long jwtExpirationMs = 86400000; // 1 ngày

        public String generateToken(String username, String email, List<String> roles, int status) {
            return Jwts.builder()
                    .setSubject(username)
                    .claim("email", email)
                    .claim("roles", roles)
                    .claim("status", status)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                    .signWith(SignatureAlgorithm.HS512, jwtSecret)
                    .compact();
        }


        public String extractUsername(String token) {
            return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
        }

        public boolean validateToken(String token) {
            try {
                Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
                return true;
            } catch (Exception e) {
                return false;
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
    }
