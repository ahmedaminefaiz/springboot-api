package org.urban.alert.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urban.alert.dto.login.LoginResponseDTO;
import org.urban.alert.dto.signup.SignupRequestDTO;
import org.urban.alert.entity.enums.RoleEnum;
import org.urban.alert.entity.User;
import org.urban.alert.entity.enums.UserStatusEnum;
import org.urban.alert.exception.PhoneAlreadyExistsException;
import org.urban.alert.repository.UserRepository;
import org.urban.alert.service.AuthService;
import org.urban.alert.service.JwtService;
import org.urban.alert.service.OtpService;
import org.urban.alert.service.mapper.AuthMapper;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final AuthMapper authMapper;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getPhone())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

    @Override
    public LoginResponseDTO login(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getStatus() != UserStatusEnum.ACTIVE) {
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
    public void register(SignupRequestDTO credentials) {
        String normalizedPhone = credentials.getPhone().replaceAll("[\\s-]+", "").replaceFirst("^0([67])", "+212$1");
        if (userRepository.existsByPhone(normalizedPhone)) {
            throw new PhoneAlreadyExistsException("Phone number already registered");
        }

        User user = new User(
                credentials.getEmail(),
                normalizedPhone,
                credentials.getNom(),
                credentials.getPrenom(),
                credentials.getDateNaissance(),
                credentials.getVille(),
                passwordEncoder.encode(credentials.getPassword()),
                credentials.getRole()
        );
        userRepository.save(user);

        otpService.sendOtp(normalizedPhone);
    }

    @Override
    @Transactional
    public LoginResponseDTO verifyPhone(String phone, String code) {
        if (!otpService.verifyCode(phone, code)) {
            throw new RuntimeException("Invalid or expired verification code");
        }

        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPhoneVerified(true);

        if (user.getRole() == RoleEnum.CITOYEN) {
            user.setStatus(UserStatusEnum.ACTIVE);
            userRepository.save(user);
            return authMapper.toLoginResponse(user, jwtService.generateToken(user));
        }

        user.setStatus(UserStatusEnum.PENDING_APPROVAL);
        userRepository.save(user);
        return null;
    }
}