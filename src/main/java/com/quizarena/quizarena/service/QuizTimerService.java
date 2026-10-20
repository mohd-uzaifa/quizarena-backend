
package com.quizarena.quizarena.service;

import com.quizarena.quizarena.entity.Quiz;
import com.quizarena.quizarena.entity.QuizStatus;
import com.quizarena.quizarena.repository.QuestionRepository;
import com.quizarena.quizarena.repository.QuizRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuizTimerService {

    private static final int TRANSITION_DELAY_SECONDS = 2;

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    private final QuizService quizService;

    private final Map<Long, TransitionState> expiredQuestions =
            new ConcurrentHashMap<>();

    public QuizTimerService(
            QuizRepository quizRepository,
            QuestionRepository questionRepository,
            QuestionService questionService,
            QuizService quizService) {

        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.questionService = questionService;
        this.quizService = quizService;
    }

    private int getTimeLimitSeconds(Quiz quiz) {
        return switch (quiz.getDifficulty()) {
            case EASY -> 15;
            case MEDIUM -> 20;
            case HARD -> 30;
        };
    }

    @Scheduled(fixedRate = 500)
    public void checkQuestionTimers() {
        LocalDateTime now = LocalDateTime.now();

        for (Quiz quiz : quizRepository.findByStatus(QuizStatus.LIVE)) {

            Long quizId = quiz.getId();
            int currentQuestion = quiz.getCurrentQuestionNumber();
            LocalDateTime startedAt = quiz.getCurrentQuestionStartedAt();

            if (startedAt == null || currentQuestion < 1) {
                continue;
            }

            TransitionState state = expiredQuestions.get(quizId);

            // Discard stale transition data if the question has changed.
            if (state != null && state.questionNumber() != currentQuestion) {
                expiredQuestions.remove(quizId, state);
                state = null;
            }

            long elapsedSeconds = ChronoUnit.SECONDS.between(
                    startedAt,
                    now
            );

            if (elapsedSeconds < getTimeLimitSeconds(quiz)) {
                continue;
            }

            // Record when this question first expired.
            if (state == null) {
                expiredQuestions.putIfAbsent(
                        quizId,
                        new TransitionState(currentQuestion, now)
                );
                state = expiredQuestions.get(quizId);
            }

            // Wait five seconds after expiry before advancing.
            if (state == null
                    || state.questionNumber() != currentQuestion
                    || ChronoUnit.SECONDS.between(
                    state.expiredAt(), now
            ) < TRANSITION_DELAY_SECONDS) {
                continue;
            }

            int nextQuestionNumber = currentQuestion + 1;
            long totalQuestions = questionRepository.countByQuizId(quizId);

            if (nextQuestionNumber > totalQuestions) {
                quizService.finishQuiz(quizId);
                expiredQuestions.remove(quizId);
                continue;
            }

            questionService.broadcastQuestion(quizId, nextQuestionNumber);
            expiredQuestions.remove(quizId);
        }
    }

    private record TransitionState(
            int questionNumber,
            LocalDateTime expiredAt
    ) {
    }
}
