
package com.quizarena.quizarena.entity;
import com.quizarena.quizarena.entity.Quiz;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private int currentQuestionNumber = 1;

    private LocalDateTime currentQuestionStartedAt;

    public Student() {
    }

    public Student(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public int getCurrentQuestionNumber() {
        return currentQuestionNumber;
    }

    public void setCurrentQuestionNumber(int currentQuestionNumber) {
        this.currentQuestionNumber = currentQuestionNumber;
    }

    public LocalDateTime getCurrentQuestionStartedAt() {
        return currentQuestionStartedAt;
    }

    public void setCurrentQuestionStartedAt(LocalDateTime currentQuestionStartedAt) {
        this.currentQuestionStartedAt = currentQuestionStartedAt;
    }
}
