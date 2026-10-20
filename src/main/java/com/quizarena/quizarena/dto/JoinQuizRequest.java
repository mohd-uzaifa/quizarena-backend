
package com.quizarena.quizarena.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class JoinQuizRequest {

    @NotBlank(message = "Room code is required")
    @Size(min = 6, max = 6, message = "Room code must be 6 characters")
    private String roomCode;

    @NotBlank(message = "Student name is required")
    @Size(max = 50, message = "Student name cannot exceed 50 characters")
    private String studentName;

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
