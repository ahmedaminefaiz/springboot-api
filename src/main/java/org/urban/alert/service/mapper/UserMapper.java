package org.urban.alert.service.mapper;

import org.mapstruct.Mapper;
import org.urban.alert.dto.usersummary.UserSummaryResponseDTO;
import org.urban.alert.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserSummaryResponseDTO toSummaryResponse(User user);

    List<UserSummaryResponseDTO> toSummaryResponseList(List<User> users);
}