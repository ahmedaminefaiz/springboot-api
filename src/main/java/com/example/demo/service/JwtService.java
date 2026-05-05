package com.example.demo.service;

import com.example.demo.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(User user);
    String extractPhone(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
}