
package com.quizarena.quizarena.controller;

import com.quizarena.quizarena.dto.SubmitAnswerRequest;
import com.quizarena.quizarena.dto.SubmitAnswerResponse;
import com.quizarena.quizarena.service.StudentAnswerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/answers")
public class StudentAnswerController {

    private final StudentAnswerService studentAnswerService;

    public StudentAnswerController(
            StudentAnswerService studentAnswerService
    ) {
        this.studentAnswerService = studentAnswerService;
    }

    @PostMapping
    public ResponseEntity<SubmitAnswerResponse> submitAnswer(
            @Valid @RequestBody SubmitAnswerRequest request
    ) {
        SubmitAnswerResponse response =
                studentAnswerService.submitAnswer(request);

        return ResponseEntity.ok(response);
    }
}
