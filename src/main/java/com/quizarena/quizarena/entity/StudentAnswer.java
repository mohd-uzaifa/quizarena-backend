
package com.quizarena.quizarena.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "student_answers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_question_answer",
                        columnNames = {"student_id", "question_id"}
                )
        }
)
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CorrectOption selectedOption;

    @Column(nullable = false)
    private boolean correct;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    public Long getId() { return id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public CorrectOption getSelectedOption() { return selectedOption; }
    public void setSelectedOption(CorrectOption selectedOption) {
        this.selectedOption = selectedOption;
    }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
