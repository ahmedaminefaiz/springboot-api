package org.urban.alert.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.urban.alert.dto.login.LoginResponseDTO;
import org.urban.alert.dto.signup.SignupRequestDTO;

public interface AuthService extends UserDetailsService {

    LoginResponseDTO login(String phone);

    void register(SignupRequestDTO credentials);

    LoginResponseDTO verifyPhone(String phone, String code);
}