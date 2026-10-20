package com.quizarena.quizarena.repository;

import com.quizarena.quizarena.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
Optional<Teacher> findByEmail(String email);
}
