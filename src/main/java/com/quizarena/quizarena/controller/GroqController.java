
package com.quizarena.quizarena.controller;

import com.quizarena.quizarena.service.GroqService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.quizarena.quizarena.dto.GenerateQuestionsRequest;
import com.quizarena.quizarena.dto.GeneratedQuestionsResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/api/groq")
public class GroqController {

    private final GroqService groqService;

    public GroqController(GroqService groqService) {
        this.groqService = groqService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testGroq() {
        return ResponseEntity.ok(groqService.testGroq());
    }
    @PostMapping("/generate")
    public ResponseEntity<GeneratedQuestionsResponse> generateQuestions(
            @Valid @RequestBody GenerateQuestionsRequest request) {

        GeneratedQuestionsResponse response = groqService.generateQuestions(
                request.getTopic(),
                request.getDifficulty().name(),
                request.getQuestionCount(),
                Collections.emptyList()
        );

        return ResponseEntity.ok(response);
    }
}
