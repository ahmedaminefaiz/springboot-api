package org.urban.alert.service.mapper;

import org.mapstruct.Mapper;
import org.urban.alert.dto.UserSummaryResponse;
import org.urban.alert.entity.User;

import java.util.List;

/**
 * MapStruct mapper for {@link User} entity to DTO conversions.
 *
 * <p>Used in the user management layer to project user data into lightweight
 * {@link UserSummaryResponse} objects for API consumers.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a single {@link User} entity to a {@link UserSummaryResponse}.
     *
     * @param user the user entity to convert
     * @return a {@link UserSummaryResponse} containing the user's summary data
     */
    UserSummaryResponse toSummaryResponse(User user);

    /**
     * Converts a list of {@link User} entities to a list of {@link UserSummaryResponse}.
     *
     * @param users the list of user entities to convert
     * @return a list of {@link UserSummaryResponse}; empty list if input is empty
     */
    List<UserSummaryResponse> toSummaryResponseList(List<User> users);
}