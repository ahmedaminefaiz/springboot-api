package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.SignupRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
              .withUsername(user.getEmail())
              .password(user.getPassword())   // already hashed in DB
              .roles("USER")
              .build();
    }

    public void register(SignupRequest credentials) {
        if(userRepository.existsByEmail(credentials.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));

        User user = new User(
            credentials.getEmail(),
            credentials.getNom(),
            credentials.getPrenom(),
            credentials.getDateNaissance(),
            credentials.getVille(),
            credentials.getPassword() 
        );
        
        userRepository.save(user);
    }
}
