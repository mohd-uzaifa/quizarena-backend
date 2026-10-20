
package com.quizarena.quizarena.controller;

import com.quizarena.quizarena.dto.GenerateQuizQuestionsRequest;
import com.quizarena.quizarena.dto.QuestionResponse;
import com.quizarena.quizarena.entity.Question;
import com.quizarena.quizarena.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes/{quizId}/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping
    public ResponseEntity<QuestionResponse> addQuestion(
            @PathVariable Long quizId,
            @Valid @RequestBody Question question) {

        Question savedQuestion =
                questionService.addQuestion(quizId, question);

        return ResponseEntity.ok(toResponse(savedQuestion));
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @PathVariable Long quizId) {

        List<QuestionResponse> responses =
                questionService.getQuestionByQuizId(quizId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @Valid @RequestBody Question question) {

        Question updatedQuestion =
                questionService.updateQuestion(
                        quizId, questionId, question);

        return ResponseEntity.ok(toResponse(updatedQuestion));
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long quizId,
            @PathVariable Long questionId) {

        questionService.deleteQuestion(quizId, questionId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generate-questions")
    public ResponseEntity<List<QuestionResponse>> generateQuestions(
            @PathVariable Long quizId,
            @Valid @RequestBody GenerateQuizQuestionsRequest request) {

        List<QuestionResponse> generatedQuestions =
                questionService.generateQuestionsForQuiz(
                        quizId,
                        request.getQuestionCount()
                );

        return ResponseEntity.ok(generatedQuestions);
    }

    @PostMapping("/{questionId}/regenerate")
    public ResponseEntity<QuestionResponse> regenerateQuestion(
            @PathVariable Long quizId,
            @PathVariable Long questionId
    ) {
        QuestionResponse response =
                questionService.regenerateQuestion(quizId, questionId);

        return ResponseEntity.ok(response);
    }


    private QuestionResponse toResponse(Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getQuestionText(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD(),
                question.getCorrectOption(),
                question.getRegenerationCount(),
                question.getQuiz().getId()
        );
    }
}
