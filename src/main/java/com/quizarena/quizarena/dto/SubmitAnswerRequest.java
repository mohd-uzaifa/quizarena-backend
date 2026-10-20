
package com.quizarena.quizarena.dto;

import com.quizarena.quizarena.entity.CorrectOption;
import jakarta.validation.constraints.NotNull;

public class SubmitAnswerRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotNull(message = "Selected option is required")
    private CorrectOption selectedOption;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public CorrectOption getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(CorrectOption selectedOption) {
        this.selectedOption = selectedOption;
    }
}
