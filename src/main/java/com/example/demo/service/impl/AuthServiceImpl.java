package com.example.demo.service.impl;

import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.SignupRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.UserStatus;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.JwtService;
import com.example.demo.service.OtpService;
import com.example.demo.service.mapper.AuthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private AuthMapper authMapper;

    @Override
    public UserDetails loadUserByUsername(String phone) throws
            UsernameNotFoundException {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getPhone())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

    @Override
    public LoginResponse login(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            String message = switch (user.getStatus()) {
                case PENDING_PHONE_VERIFICATION -> "Phone number not verified yet";
                case PENDING_APPROVAL -> "Account is pending approval";
                case REJECTED -> "Account has been rejected";
                default -> "Account is not active";
            };
            throw new RuntimeException(message);
        }

        return authMapper.toLoginResponse(user, jwtService.generateToken(user));
    }

    @Override
    @Transactional
    public void register(SignupRequest credentials) {
        if (userRepository.existsByPhone(credentials.getPhone())) {
            throw new RuntimeException("Phone number already registered");
        }

        User user = new User(
                credentials.getEmail(),
                credentials.getPhone(),
                credentials.getNom(),
                credentials.getPrenom(),
                credentials.getDateNaissance(),
                credentials.getVille(),
                passwordEncoder.encode(credentials.getPassword()),
                credentials.getRole()
        );
        userRepository.save(user);

        otpService.sendOtp(credentials.getPhone());
    }

    @Override
    @Transactional
    public LoginResponse verifyPhone(String phone, String code) {
        if (!otpService.verifyCode(phone, code)) {
            throw new RuntimeException("Invalid or expired verification code");
        }

        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPhoneVerified(true);

        if (user.getRole() == Role.CITOYEN) {
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
            return authMapper.toLoginResponse(user, jwtService.generateToken(user));
        }

        user.setStatus(UserStatus.PENDING_APPROVAL);
        userRepository.save(user);
        return null;
    }
}