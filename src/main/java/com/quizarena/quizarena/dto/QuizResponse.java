
package com.quizarena.quizarena.dto;

import com.quizarena.quizarena.entity.Difficulty;
import com.quizarena.quizarena.entity.QuizStatus;

import java.time.LocalDateTime;

public class QuizResponse {

    private Long id;
    private String topic;
    private Difficulty difficulty;
    private String roomCode;
    private QuizStatus status;
    private LocalDateTime createdAt;
    private Long teacherId;

    public QuizResponse() {}

    public QuizResponse(
            Long id,
            String topic,
            Difficulty difficulty,
            String roomCode,
            QuizStatus status,
            LocalDateTime createdAt,
            Long teacherId) {

        this.id = id;
        this.topic = topic;
        this.difficulty = difficulty;
        this.roomCode = roomCode;
        this.status = status;
        this.createdAt = createdAt;
        this.teacherId = teacherId;
    }

    public Long getId() { return id; }
    public String getTopic() { return topic; }
    public Difficulty getDifficulty() { return difficulty; }
    public String getRoomCode() { return roomCode; }
    public QuizStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getTeacherId() { return teacherId; }
}
