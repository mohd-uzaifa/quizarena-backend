package com.quizarena.quizarena.dto;

public class StudentResponse {

    private Long id;
    private String name;
    private String roomCode;
    private Long quizId;

    public StudentResponse(Long id, String name, String roomCode, Long quizId) {
        this.id = id;
        this.name = name;
        this.roomCode = roomCode;
        this.quizId = quizId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public Long getQuizId() {
        return quizId;
    }
}