
package com.quizarena.quizarena.dto;

public class LeaderboardEntryResponse {

    private Long studentId;
    private String studentName;
    private int totalScore;
    private int rank;

    public LeaderboardEntryResponse(
            Long studentId,
            String studentName,
            int totalScore,
            int rank) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.totalScore = totalScore;
        this.rank = rank;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getRank() {
        return rank;
    }
}
