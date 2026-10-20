
package com.quizarena.quizarena.repository;

import com.quizarena.quizarena.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentAnswerRepository
        extends JpaRepository<StudentAnswer, Long> {

    boolean existsByStudentIdAndQuestionId(
            Long studentId,
            Long questionId
    );

    @Query("SELECT COALESCE(SUM(sa.score), 0) FROM StudentAnswer sa WHERE sa.student.id = :studentId")
    int calculateTotalScore(@Param("studentId") Long studentId);


    Optional<StudentAnswer> findByStudentIdAndQuestionId(
            Long studentId,
            Long questionId
    );
}
