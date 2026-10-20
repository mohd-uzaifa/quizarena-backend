
package com.quizarena.quizarena.dto;

public class LobbyStudentResponse {

    private Long studentId;
    private String studentName;
    private String roomCode;

    public LobbyStudentResponse(Long studentId, String studentName, String roomCode) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.roomCode = roomCode;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getRoomCode() {
        return roomCode;
    }
}
