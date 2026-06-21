package org.urban.alert.service.mapper;

import org.mapstruct.*;
import org.urban.alert.dto.alert.CreateAlertRequestDTO;
import org.urban.alert.dto.alert.AlertResponseDTO;
import org.urban.alert.dto.alert.UpdateAlertRequestDTO;
import org.urban.alert.entity.Alert;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {UserMapper.class, ProblemTypeMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface AlertMapper {

    // ========== Create DTO to Entity ==========

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "NEW")
    @Mapping(target = "priority", defaultValue = "MEDIUM")
    @Mapping(target = "isAnonymous", defaultValue = "false")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "ticket", ignore = true)
    @Mapping(target = "commentaires", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "videos", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Alert createAlertRequestToEntity(CreateAlertRequestDTO request);

    // ========== Entity to Response DTO ==========

    @Mapping(source = "category", target = "category")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "ticket.id", target = "ticketId")
    AlertResponseDTO entityToAlertResponse(Alert alert);

    // ========== Update DTO to Entity ==========

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "ticket", ignore = true)
    @Mapping(target = "commentaires", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "videos", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateAlertFromRequest(
            UpdateAlertRequestDTO request,
            @MappingTarget Alert alert);
}