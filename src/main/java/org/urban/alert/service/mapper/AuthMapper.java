package org.urban.alert.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.urban.alert.dto.LoginResponse;
import org.urban.alert.entity.User;

/**
 * MapStruct mapper for authentication-related DTO conversions.
 *
 * <p>Converts a {@link User} entity combined with a JWT token string into a
 * {@link LoginResponse} suitable for returning to API clients after a successful login
 * or phone verification.
 */
@Mapper(componentModel = "spring")
public interface AuthMapper {

    /**
     * Builds a {@link LoginResponse} from a user entity and a pre-generated JWT token.
     *
     * <p>The {@code role} and {@code status} fields are derived from their respective enum names.
     *
     * @param user  the authenticated user entity
     * @param token the JWT token string generated for the session
     * @return a fully populated {@link LoginResponse}
     */
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(target = "status", expression = "java(user.getStatus().name())")
    LoginResponse toLoginResponse(User user, String token);
}