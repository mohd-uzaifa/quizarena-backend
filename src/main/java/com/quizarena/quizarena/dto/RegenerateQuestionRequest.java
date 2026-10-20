
package com.quizarena.quizarena.dto;

import jakarta.validation.constraints.NotNull;

public class RegenerateQuestionRequest {

    @NotNull
    private Long questionId;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}
