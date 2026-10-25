package com.quizarena.quizarena.dto;

import com.quizarena.quizarena.entity.CorrectOption;

public class QuestionResponse {

    private Long id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private CorrectOption correctOption;
    private Integer regenerationCount;
    private Long quizId;
    private String concept;

    public QuestionResponse() {}

    public QuestionResponse(
            Long id,
            String questionText,
            String optionA,
            String optionB,
            String optionC,
            String optionD,
            CorrectOption correctOption,
            Integer regenerationCount,
            Long quizId,
            String concept) {

        this.id = id;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.regenerationCount = regenerationCount;
        this.quizId = quizId;
        this.concept = concept;
    }

    public Long getId() { return id; }
    public String getQuestionText() { return questionText; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public CorrectOption getCorrectOption() { return correctOption; }
    public Integer getRegenerationCount() { return regenerationCount; }
    public Long getQuizId() { return quizId; }

    // NEW: Getter and Setter for concept
    public String getConcept() { return concept; }
    public void setConcept(String concept) { this.concept = concept; }
}