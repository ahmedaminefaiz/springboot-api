package org.urban.alert.service;

/**
 * Contract for One-Time Password (OTP) lifecycle management.
 *
 * <p>Abstracts the generation, delivery, and verification of OTP codes
 * used during the phone number verification step of the registration flow.
 */
public interface OtpService {

    /**
     * Generates an OTP code and dispatches it to the given phone number.
     *
     * <p>Any previously active codes for the same phone are invalidated before
     * the new code is persisted and sent, ensuring only one valid code exists at a time.
     *
     * @param phone the recipient's phone number in international format
     */
    void sendOtp(String phone);

    /**
     * Verifies that the provided code matches the latest valid OTP for the phone.
     *
     * <p>A code is considered valid only if it has not been used and has not yet expired.
     * On successful verification the code is marked as used to prevent reuse.
     *
     * @param phone the phone number to verify against
     * @param code  the OTP code submitted by the user
     * @return {@code true} if the code is correct and still valid, {@code false} otherwise
     */
    boolean verifyCode(String phone, String code);
}