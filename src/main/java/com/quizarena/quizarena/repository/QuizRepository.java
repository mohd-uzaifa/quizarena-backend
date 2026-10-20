package com.quizarena.quizarena.repository;

import com.quizarena.quizarena.entity.Quiz;
import com.quizarena.quizarena.entity.QuizStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByRoomCode(String roomCode);
    boolean existsByRoomCode(String roomCode);
    List<Quiz> findByStatus(QuizStatus status);
    List<Quiz> findByTeacherIdOrderByIdDesc(Long teacherId);
}