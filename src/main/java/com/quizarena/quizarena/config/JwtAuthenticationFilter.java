
package com.quizarena.quizarena.config;

import com.quizarena.quizarena.entity.Teacher;
import com.quizarena.quizarena.repository.TeacherRepository;
import com.quizarena.quizarena.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TeacherRepository teacherRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            TeacherRepository teacherRepository) {
        this.jwtService = jwtService;
        this.teacherRepository = teacherRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            String email = jwtService.extractEmail(token);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                Teacher teacher = teacherRepository.findByEmail(email).orElse(null);

                if (teacher != null && jwtService.isTokenValid(token, teacher)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    teacher.getEmail(),
                                    null,
                                    Collections.emptyList());

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }
        } catch (JwtException | IllegalArgumentException exception) {
            // Invalid or expired token: leave the request unauthenticated.
        }

        filterChain.doFilter(request, response);
    }
}
