package org.urban.alert.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.urban.alert.dto.LoginResponse;
import org.urban.alert.dto.SignupRequest;

/**
 * Contract for authentication operations in the Urban Alert application.
 *
 * <p>Extends Spring Security's {@link UserDetailsService} to integrate with the
 * security filter chain, while exposing business-level operations for registration,
 * login, and phone number verification via OTP.
 */
public interface AuthService extends UserDetailsService {

    /**
     * Authenticates a user by phone number and returns a JWT-bearing response.
     *
     * <p>Only users with an {@code ACTIVE} status are allowed to log in.
     * Any other status results in a descriptive runtime exception.
     *
     * @param phone the registered phone number of the user
     * @return a {@link LoginResponse} containing the JWT token and user details
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if no user is registered with the given phone
     * @throws RuntimeException if the user's account is not active
     */
    LoginResponse login(String phone);

    /**
     * Registers a new user account and triggers an OTP verification message via WhatsApp.
     *
     * <p>The created account starts with {@code PENDING_PHONE_VERIFICATION} status.
     * Citizens are auto-activated after phone verification; Agents and Super-Agents
     * require additional admin approval.
     *
     * @param credentials the sign-up payload containing the user's personal details and desired role
     * @throws org.urban.alert.exception.PhoneAlreadyExistsException if the phone number is already registered
     */
    void register(SignupRequest credentials);

    /**
     * Verifies a user's phone number using the OTP code received via WhatsApp.
     *
     * <p>On success, citizens are immediately activated and receive a JWT token.
     * Agents and Super-Agents are moved to {@code PENDING_APPROVAL} status and
     * {@code null} is returned, pending admin review.
     *
     * @param phone the phone number to verify
     * @param code  the OTP code sent to the user's phone
     * @return a {@link LoginResponse} for citizens, or {@code null} for roles requiring approval
     * @throws RuntimeException if the code is invalid or expired
     */
    LoginResponse verifyPhone(String phone, String code);
}