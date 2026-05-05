package com.example.demo.service;

import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.SignupRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {
    LoginResponse login(String phone);
    void register(SignupRequest credentials);
    LoginResponse verifyPhone(String phone, String code);
}