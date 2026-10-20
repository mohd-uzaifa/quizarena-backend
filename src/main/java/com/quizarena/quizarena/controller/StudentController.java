
package com.quizarena.quizarena.controller;

import com.quizarena.quizarena.dto.JoinQuizRequest;
import com.quizarena.quizarena.dto.StudentResponse;
import com.quizarena.quizarena.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/join")
    public ResponseEntity<StudentResponse> joinQuiz(
            @Valid @RequestBody JoinQuizRequest request) {

        StudentResponse response = studentService.joinQuiz(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<StudentResponse>> getStudentsByQuiz(
            @PathVariable Long quizId) {

        return ResponseEntity.ok(
                studentService.getStudentsByQuizId(quizId)
        );
    }
}
