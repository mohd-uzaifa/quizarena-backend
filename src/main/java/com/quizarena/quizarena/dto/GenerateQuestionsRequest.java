package com.quizarena.quizarena.dto;

import com.quizarena.quizarena.entity.Difficulty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class GenerateQuestionsRequest {

    // Validation removed because frontend does not send these in the batch loop.
    // The service layer fetches them from the saved Quiz entity instead.
    private String topic;
    private Difficulty difficulty;

    @NotNull
    @Min(1) // CRITICAL: Reduced from 5 to 1 so smaller final batches are allowed
    @Max(15)
    private Integer questionCount;

    // NEW: Accepts the list of concepts to avoid duplicates
    private List<String> existingConcepts;

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

    public List<String> getExistingConcepts() {
        return existingConcepts;
    }

    public void setExistingConcepts(List<String> existingConcepts) {
        this.existingConcepts = existingConcepts;
    }
}