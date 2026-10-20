
package com.quizarena.quizarena.dto;

public class LiveQuestionResponse {

    private Long questionId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int questionNumber;
    private int totalQuestions;
    private int timeLimitSeconds;

    public LiveQuestionResponse(
            Long questionId,
            String questionText,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            int questionNumber,
            int totalQuestions,
            int timeLimitSeconds) {

        this.questionId = questionId;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.questionNumber = questionNumber;
        this.totalQuestions = totalQuestions;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public Long getQuestionId() { return questionId; }
    public String getQuestionText() { return questionText; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public int getQuestionNumber() { return questionNumber; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getTimeLimitSeconds() { return timeLimitSeconds; }
}
