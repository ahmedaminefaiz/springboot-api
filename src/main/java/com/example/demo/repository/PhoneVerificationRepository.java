package com.example.demo.repository;

import com.example.demo.entity.PhoneVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {

    Optional<PhoneVerification>
    findTopByPhoneAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc( String phone, LocalDateTime now);

    @Modifying
    @Query("UPDATE PhoneVerification pv SET pv.used = true WHERE pv.phone = :phone AND pv.used = false")
    void invalidatePreviousCodes(@Param("phone") String phone);
}