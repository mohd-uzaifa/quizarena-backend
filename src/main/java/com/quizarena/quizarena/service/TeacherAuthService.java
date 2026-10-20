
package com.quizarena.quizarena.service;

import com.quizarena.quizarena.dto.AuthResponse;
import com.quizarena.quizarena.dto.TeacherLoginRequest;
import com.quizarena.quizarena.dto.TeacherRegisterRequest;
import com.quizarena.quizarena.entity.Teacher;
import com.quizarena.quizarena.repository.TeacherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TeacherAuthService {

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public TeacherAuthService(
            TeacherRepository teacherRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(TeacherRegisterRequest request) {
        if (teacherRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email is already registered");
        }

        Teacher teacher = new Teacher();
        teacher.setName(request.getName());
        teacher.setEmail(request.getEmail());
        teacher.setPassword(passwordEncoder.encode(request.getPassword()));

        Teacher savedTeacher = teacherRepository.save(teacher);

        String token = jwtService.generateToken(savedTeacher);

        return new AuthResponse(
                savedTeacher.getId(),
                savedTeacher.getName(),
                savedTeacher.getEmail(),
                token);
    }

    public AuthResponse login(TeacherLoginRequest request) {
        Teacher teacher = teacherRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(), teacher.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token = jwtService.generateToken(teacher);

        return new AuthResponse(
                teacher.getId(),
                teacher.getName(),
                teacher.getEmail(),
                token);
    }
}
