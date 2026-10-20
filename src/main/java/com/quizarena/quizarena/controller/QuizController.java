package com.quizarena.quizarena.controller;

import com.quizarena.quizarena.dto.CreateQuizRequest;
import com.quizarena.quizarena.dto.QuizResponse;
import com.quizarena.quizarena.entity.Quiz;
import com.quizarena.quizarena.repository.QuestionRepository;
import com.quizarena.quizarena.service.QuestionService;
import com.quizarena.quizarena.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final QuestionService questionService;
    private final QuestionRepository questionRepository;
    private final SimpMessagingTemplate messagingTemplate;
    public QuizController(
            QuizService quizService,
            QuestionService questionService,
            QuestionRepository questionRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.quizService = quizService;
        this.questionService = questionService;
        this.questionRepository = questionRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    public ResponseEntity<QuizResponse> createQuiz(
            @RequestBody CreateQuizRequest request) {
        return ResponseEntity.ok(toResponse(quizService.createQuiz(request)));
    }

    @PostMapping("/{quizId}/start")
    public ResponseEntity<QuizResponse> startQuiz(
            @PathVariable Long quizId) {
        return ResponseEntity.ok(toResponse(quizService.startQuiz(quizId)));
    }

    @PostMapping("/{quizId}/go-live")
    public ResponseEntity<QuizResponse> goLive(
            @PathVariable Long quizId) {
        Quiz quiz = quizService.goLive(quizId);
        questionService.broadcastQuestion(quizId, 1);
        return ResponseEntity.ok(toResponse(quiz));
    }

    @PostMapping("/{quizId}/finish")
    public ResponseEntity<QuizResponse> finishQuiz(
            @PathVariable Long quizId) {

        Quiz quiz = quizService.finishQuiz(quizId);

        // Broadcast the "Game Over" signal to all students!
        messagingTemplate.convertAndSend("/topic/quiz/" + quizId + "/finish", "FINISHED");

        return ResponseEntity.ok(toResponse(quiz));
    }

    @PostMapping("/{quizId}/next-question")
    public ResponseEntity<String> nextQuestion(
            @PathVariable Long quizId) {

        Quiz quiz = quizService.getQuizById(quizId);

        if (quiz.getCurrentQuestionNumber() < 1) {
            throw new IllegalStateException(
                    "The quiz has not started broadcasting questions");
        }

        long totalQuestions =
                questionRepository.countByQuizId(quizId);

        int nextQuestionNumber =
                quiz.getCurrentQuestionNumber() + 1;

        if (nextQuestionNumber > totalQuestions) {
            quizService.finishQuiz(quizId);
            return ResponseEntity.ok("Quiz finished");
        }

        questionService.broadcastQuestion(quizId, nextQuestionNumber);

        return ResponseEntity.ok("Next question broadcast successfully");
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponse> getQuiz(
            @PathVariable Long quizId) {

        Quiz quiz = quizService.getQuizById(quizId);

        return ResponseEntity.ok(toResponse(quiz));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<QuizResponse>> getTeacherQuizzes(
            @PathVariable Long teacherId) {

        List<QuizResponse> responses = quizService.getQuizzesByTeacher(teacherId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    private QuizResponse toResponse(Quiz quiz) {
        return new QuizResponse(
                quiz.getId(),
                quiz.getTopic(),
                quiz.getDifficulty(),
                quiz.getRoomCode(),
                quiz.getStatus(),
                quiz.getCreatedAt(),
                quiz.getTeacher().getId()
        );
    }
    @DeleteMapping("/{quizId}")
    public ResponseEntity<String> deleteQuiz(@PathVariable Long quizId) {
        // Find the quiz
        Quiz quiz = quizService.getQuizById(quizId);

        // Delete it (this will also delete associated questions if your entity has CascadeType.ALL)
        quizService.deleteQuiz(quizId);

        return ResponseEntity.ok("Quiz deleted successfully");
    }
}