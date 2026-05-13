package org.urban.alert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.urban.alert.entity.PhoneVerification;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * JPA repository for {@link PhoneVerification} records.
 *
 * <p>Manages the persistence of OTP codes with expiry and usage tracking,
 * supporting the phone number verification step of the registration flow.
 */
public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {

    /**
     * Retrieves the most recent unused and non-expired OTP record for a given phone number.
     *
     * <p>Results are ordered by creation date descending so that only the latest
     * issued code is considered, even if multiple valid records exist.
     *
     * @param phone the phone number to look up
     * @param now   the current timestamp used to filter out expired records
     * @return an {@link Optional} containing the latest valid OTP record, or empty if none exists
     */
    Optional<PhoneVerification>
    findTopByPhoneAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(String phone, LocalDateTime now);

    /**
     * Marks all active (unused) OTP codes for the given phone number as used.
     *
     * <p>Called before issuing a new OTP to ensure only one active code exists per phone at a time.
     *
     * @param phone the phone number whose active codes should be invalidated
     */
    @Modifying
    @Query("UPDATE PhoneVerification pv SET pv.used = true WHERE pv.phone = :phone AND pv.used = false")
    void invalidatePreviousCodes(@Param("phone") String phone);
}