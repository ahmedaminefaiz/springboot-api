package org.urban.alert.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.urban.alert.dto.LoginResponseDTO;
import org.urban.alert.entity.User;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(target = "status", expression = "java(user.getStatus().name())")
    LoginResponseDTO toLoginResponse(User user, String token);
}