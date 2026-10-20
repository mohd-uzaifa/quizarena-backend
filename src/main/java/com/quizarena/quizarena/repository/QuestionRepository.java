package com.quizarena.quizarena.repository;

import com.quizarena.quizarena.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question,Long> {
    List<Question> findByQuizIdOrderByIdAsc(Long quizId);
    long countByQuizId(Long quizId);
}
