package com.quizarena.quizarena.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class GenerateQuizQuestionsRequest {

    @NotNull
    @Min(5)
    @Max(20)
    private Integer questionCount;

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }
}