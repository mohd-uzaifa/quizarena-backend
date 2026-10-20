
package com.quizarena.quizarena.controller;

import com.quizarena.quizarena.dto.AuthResponse;
import com.quizarena.quizarena.dto.TeacherLoginRequest;
import com.quizarena.quizarena.dto.TeacherRegisterRequest;
import com.quizarena.quizarena.service.TeacherAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
public class TeacherAuthController {

    private final TeacherAuthService teacherAuthService;

    public TeacherAuthController(TeacherAuthService teacherAuthService) {
        this.teacherAuthService = teacherAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody TeacherRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teacherAuthService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody TeacherLoginRequest request) {
        return ResponseEntity.ok(teacherAuthService.login(request));
    }
}
