
package com.quizarena.quizarena.repository;

import com.quizarena.quizarena.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByQuizIdOrderByIdAsc(Long quizId);

    boolean existsByQuizIdAndNameIgnoreCase(Long quizId, String name);
}
