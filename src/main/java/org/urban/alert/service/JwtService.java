package org.urban.alert.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.urban.alert.entity.User;

/**
 * Contract for JSON Web Token (JWT) operations.
 *
 * <p>Defines the full lifecycle of a JWT: generation from a {@link User} entity,
 * claim extraction, and validation against a {@link UserDetails} principal.
 */
public interface JwtService {

    /**
     * Generates a signed JWT token embedding the given user's identity.
     *
     * @param user the authenticated user whose data is embedded in the token claims
     * @return a signed JWT string ready to be returned to the client
     */
    String generateToken(User user);

    /**
     * Extracts the phone number (token subject) from a JWT string.
     *
     * @param token the JWT string to parse
     * @return the phone number stored as the token's subject claim
     */
    String extractPhone(String token);

    /**
     * Checks whether a JWT token is valid for the given user principal.
     *
     * <p>Validation covers both signature integrity and token expiry.
     *
     * @param token       the JWT string to validate
     * @param userDetails the currently authenticated principal to match against
     * @return {@code true} if the token is valid and belongs to the principal, {@code false} otherwise
     */
    boolean isTokenValid(String token, UserDetails userDetails);
}