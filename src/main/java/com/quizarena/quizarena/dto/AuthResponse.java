
package com.quizarena.quizarena.dto;

public class AuthResponse {

    private Long teacherId;
    private String name;
    private String email;
    private String token;
    private String tokenType = "Bearer";

    public AuthResponse(
            Long teacherId,
            String name,
            String email,
            String token) {
        this.teacherId = teacherId;
        this.name = name;
        this.email = email;
        this.token = token;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }
}
