package com.example.demo.service;

public interface OtpService {
    void sendOtp(String phone);
    boolean verifyCode(String phone, String code);
}