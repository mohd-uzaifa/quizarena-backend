
package com.quizarena.quizarena.service;

import com.quizarena.quizarena.dto.SubmitAnswerRequest;
import com.quizarena.quizarena.dto.SubmitAnswerResponse;
import com.quizarena.quizarena.entity.*;
import com.quizarena.quizarena.repository.QuestionRepository;
import com.quizarena.quizarena.repository.QuizRepository;
import com.quizarena.quizarena.repository.StudentAnswerRepository;
import com.quizarena.quizarena.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class StudentAnswerService {

    private final StudentAnswerRepository answerRepository;
    private final StudentRepository studentRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final LeaderboardService leaderboardService;

    public StudentAnswerService(
            StudentAnswerRepository answerRepository,
            StudentRepository studentRepository,
            QuestionRepository questionRepository,
            QuizRepository quizRepository,
            LeaderboardService leaderboardService
    ) {
        this.answerRepository = answerRepository;
        this.studentRepository = studentRepository;
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.leaderboardService = leaderboardService;
    }

    @Transactional
    public SubmitAnswerResponse submitAnswer(SubmitAnswerRequest request) {

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Question not found"));

        Quiz quiz = quizRepository.findById(question.getQuiz().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Quiz not found"));

        if (!student.getQuiz().getId().equals(quiz.getId())) {
            throw new IllegalArgumentException(
                    "Student does not belong to this quiz");
        }

        if (quiz.getStatus() != QuizStatus.LIVE) {
            throw new IllegalStateException("Quiz is not live");
        }

        if (quiz.getCurrentQuestionNumber() < 1
                || quiz.getCurrentQuestionStartedAt() == null) {
            throw new IllegalStateException("No question is currently active");
        }

        if (answerRepository.existsByStudentIdAndQuestionId(
                student.getId(), question.getId())) {
            throw new IllegalArgumentException(
                    "You have already answered this question");
        }

        var questions =
                questionRepository.findByQuizIdOrderByIdAsc(quiz.getId());

        int expectedQuestionNumber = -1;

        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId().equals(question.getId())) {
                expectedQuestionNumber = i + 1;
                break;
            }
        }

        if (expectedQuestionNumber != quiz.getCurrentQuestionNumber()) {
            throw new IllegalArgumentException(
                    "This question is not currently active");
        }

        int timeLimitSeconds = switch (quiz.getDifficulty()) {
            case EASY -> 15;
            case MEDIUM -> 20;
            case HARD -> 30;
        };

        LocalDateTime now = LocalDateTime.now();

        long elapsedMillis = ChronoUnit.MILLIS.between(
                quiz.getCurrentQuestionStartedAt(), now);

        if (elapsedMillis < 0
                || elapsedMillis >= timeLimitSeconds * 1000L) {
            throw new IllegalArgumentException("Answer time has expired");
        }

        double responseSeconds = elapsedMillis / 1000.0;

        boolean correct =
                question.getCorrectOption() == request.getSelectedOption();

        int score = 0;

        if (correct) {
            int basePoints = switch (quiz.getDifficulty()) {
                case EASY -> 100;
                case MEDIUM -> 200;
                case HARD -> 300;
            };

            double multiplier = 1.0
                    + (timeLimitSeconds - responseSeconds)
                    / timeLimitSeconds;

            score = (int) Math.round(basePoints * multiplier);
        }

        StudentAnswer answer = new StudentAnswer();
        answer.setStudent(student);
        answer.setQuestion(question);
        answer.setSelectedOption(request.getSelectedOption());
        answer.setCorrect(correct);
        answer.setScore(score);
        answer.setSubmittedAt(now);

        answerRepository.save(answer);

        leaderboardService.broadcastLeaderboard(quiz.getId());

        return new SubmitAnswerResponse(
                student.getId(),
                question.getId(),
                correct,
                score,
                "Answer submitted successfully"
        );
    }
}
