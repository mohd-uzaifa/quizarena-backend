package com.quizarena.quizarena.dto;

import com.quizarena.quizarena.entity.Difficulty;

public class CreateQuizRequest {
    private String topic;
    private Difficulty difficulty;
    private Long teacherId;

    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
