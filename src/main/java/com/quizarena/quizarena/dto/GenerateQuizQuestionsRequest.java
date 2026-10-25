package com.quizarena.quizarena.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class GenerateQuizQuestionsRequest {

    @NotNull
    @Min(1) // CRITICAL: Reduced from 5 to 1 so smaller final batches are allowed
    @Max(15)
    private Integer questionCount;

    // NEW: Accepts the list of concepts to avoid duplicates
    private List<String> existingConcepts;

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