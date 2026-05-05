package com.example.demo.service.mapper;

import com.example.demo.dto.UserSummaryResponse;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserSummaryResponse toSummaryResponse(User user);
    List<UserSummaryResponse> toSummaryResponseList(List<User> users);
}