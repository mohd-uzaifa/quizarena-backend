
package com.quizarena.quizarena.dto;

import com.quizarena.quizarena.entity.Difficulty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class GenerateQuestionsRequest {

    @NotBlank
    @Size(max = 200)
    private String topic;

    @NotNull
    private Difficulty difficulty;

    @NotNull
    @Min(5)
    @Max(20)
    private Integer questionCount;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }
}
