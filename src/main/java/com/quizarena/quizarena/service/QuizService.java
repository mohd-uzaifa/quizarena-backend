package com.quizarena.quizarena.service;

import com.quizarena.quizarena.dto.CreateQuizRequest;
import com.quizarena.quizarena.entity.Quiz;
import com.quizarena.quizarena.entity.QuizStatus;
import com.quizarena.quizarena.entity.Teacher;
import com.quizarena.quizarena.exception.ResourceNotFoundException;
import com.quizarena.quizarena.repository.QuestionRepository;
import com.quizarena.quizarena.repository.QuizRepository;
import com.quizarena.quizarena.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QuizService {
    private final QuizRepository quizRepository;
    private final TeacherRepository teacherRepository;
    private final QuestionRepository questionRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public QuizService(
            QuizRepository quizRepository,
            TeacherRepository teacherRepository,
            QuestionRepository questionRepository) {

        this.quizRepository = quizRepository;
        this.teacherRepository = teacherRepository;
        this.questionRepository = questionRepository;
    }

    public Quiz createQuiz(CreateQuizRequest request) {
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        Quiz quiz = new Quiz();
        quiz.setTeacher(teacher);
        quiz.setTopic(request.getTopic());
        quiz.setDifficulty(request.getDifficulty());
        quiz.setStatus(QuizStatus.DRAFT);
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setRoomCode(generateRoomCode());

        return quizRepository.save(quiz);
    }

    private String generateRoomCode() {
        String roomCode;
        do {
            roomCode = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        } while (quizRepository.existsByRoomCode(roomCode));
        return roomCode;
    }

    public Quiz startQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        if (quiz.getStatus() != QuizStatus.DRAFT) {
            throw new IllegalStateException("Only draft quizzes can be started");
        }

        long questionCount = questionRepository.countByQuizId(quizId);

        if (questionCount < 5) {
            throw new IllegalStateException("A quiz must contain at least 5 questions");
        }

        if (questionCount > 20) {
            throw new IllegalStateException("A quiz cannot contain more than 20 questions");
        }

        quiz.setStatus(QuizStatus.WAITING);

        return quizRepository.save(quiz);
    }

    public Quiz goLive(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        if (quiz.getStatus() != QuizStatus.WAITING) {
            throw new IllegalStateException("Only waiting quizzes can go live");
        }

        quiz.setStatus(QuizStatus.LIVE);

        return quizRepository.save(quiz);
    }

    public Quiz finishQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        if (quiz.getStatus() != QuizStatus.LIVE) {
            throw new IllegalStateException("Only live quizzes can be finished");
        }

        quiz.setStatus(QuizStatus.FINISHED);

        return quizRepository.save(quiz);
    }

    public Quiz getQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
    }

    public List<Quiz> getQuizzesByTeacher(Long teacherId) {
        return quizRepository.findByTeacherIdOrderByIdDesc(teacherId);
    }

    @Transactional
    public void deleteQuiz(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new RuntimeException("Quiz not found");
        }

        // 1. Delete all student answers linked to the questions in this quiz
        entityManager.createNativeQuery(
                "DELETE FROM student_answers WHERE question_id IN (SELECT id FROM questions WHERE quiz_id = :quizId)"
        ).setParameter("quizId", quizId).executeUpdate();

        // 2. Delete all students who joined this quiz
        entityManager.createNativeQuery(
                "DELETE FROM students WHERE quiz_id = :quizId"
        ).setParameter("quizId", quizId).executeUpdate();

        // 3. Delete the questions themselves
        entityManager.createNativeQuery(
                "DELETE FROM questions WHERE quiz_id = :quizId"
        ).setParameter("quizId", quizId).executeUpdate();

        // 4. Finally, safely delete the quiz
        quizRepository.deleteById(quizId);
    }
}