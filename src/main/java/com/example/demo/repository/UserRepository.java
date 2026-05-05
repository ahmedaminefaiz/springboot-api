package com.example.demo.repository;

import com.example.demo.entity.User;

import com.example.demo.entity.Role;
import com.example.demo.entity.UserStatus;
import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    List<User> findByRoleAndStatus(Role role, UserStatus status);
}
