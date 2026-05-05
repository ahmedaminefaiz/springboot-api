package com.example.demo.dto;

public class LoginResponse {
    private String token;
    private String role;
    private String status;

    public LoginResponse() {}

    public LoginResponse(String token, String role, String status) {
        this.token = token;
        this.role = role;
        this.status = status;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}