
package com.quizarena.quizarena.dto;

public class SubmitAnswerResponse {

    private Long studentId;
    private Long questionId;
    private boolean correct;
    private int score;
    private String message;

    public SubmitAnswerResponse(
            Long studentId,
            Long questionId,
            boolean correct,
            int score,
            String message
    ) {
        this.studentId = studentId;
        this.questionId = questionId;
        this.correct = correct;
        this.score = score;
        this.message = message;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public boolean isCorrect() {
        return correct;
    }

    public int getScore() {
        return score;
    }

    public String getMessage() {
        return message;
    }
}
