package com.example.demo.security;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(User user) {
        String roles = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getName())
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public String extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", String.class));
    }


    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        final String email = extractEmail(token);
        return (username.equals(userDetails.getUsername())
                && email.equals(((User) userDetails).getEmail())
                && !isTokenExpired(token));
    }

}
