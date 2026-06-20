package org.urban.alert.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.urban.alert.entity.enums.RoleEnum;
import org.urban.alert.entity.User;
import org.urban.alert.entity.enums.UserStatusEnum;

/**
 * JPA repository for {@link User} entities.
 *
 * <p>Extends {@link JpaRepository} with business-specific query methods for
 * phone- and email-based lookups, existence checks, and role/status filtering.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email address.
     *
     * @param email the email address to search for
     * @return an {@link Optional} containing the matching user, or empty if none found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user with the given email address already exists.
     *
     * @param email the email address to check
     * @return {@code true} if a user with this email exists, {@code false} otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by phone number.
     *
     * @param phone the phone number to search for
     * @return an {@link Optional} containing the matching user, or empty if none found
     */
    Optional<User> findByPhone(String phone);

    /**
     * Checks whether a user with the given phone number already exists.
     *
     * @param phone the phone number to check
     * @return {@code true} if a user with this phone exists, {@code false} otherwise
     */
    boolean existsByPhone(String phone);

    /**
     * Finds all users matching the specified role and account status.
     *
     * @param role   the role to filter by (e.g. {@link Role#AGENT})
     * @param status the account status to filter by (e.g. {@link UserStatus#PENDING_APPROVAL})
     * @return a list of matching users; empty list if none found
     */
    List<User> findByRoleAndStatus(RoleEnum role, UserStatusEnum status);

    List<User> findByRoleAndSupervisorId(RoleEnum role, Long supervisorId);
}
