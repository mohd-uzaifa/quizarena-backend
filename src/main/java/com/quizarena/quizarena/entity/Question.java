package com.quizarena.quizarena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false,length=1000)
    private String questionText;
    @NotBlank
    @Column(nullable = false)
    private String optionA;
    @NotBlank
    @Column(nullable = false)
    private String optionB;
    @NotBlank
    @Column(nullable = false)
    private String optionC;
    @NotBlank
    @Column(nullable = false)
    private String optionD;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CorrectOption correctOption;
    @Column(nullable = false)
    private Integer regenerationCount=0;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="quiz_id",nullable = false)
    private Quiz quiz;
    public Question() {}

    public Long getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public CorrectOption getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(CorrectOption correctOption) {
        this.correctOption = correctOption;
    }

    public Integer getRegenerationCount() {
        return regenerationCount;
    }

    public void setRegenerationCount(Integer regenerationCount) {
        this.regenerationCount = regenerationCount;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
}
