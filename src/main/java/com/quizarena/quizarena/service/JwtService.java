
package com.quizarena.quizarena.service;

import com.quizarena.quizarena.entity.Teacher;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Teacher teacher) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(teacher.getEmail())
                .claim("teacherId", teacher.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(86400)))
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, Teacher teacher) {
        Claims claims = extractClaims(token);

        return claims.getSubject().equals(teacher.getEmail())
                && claims.getExpiration().after(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
