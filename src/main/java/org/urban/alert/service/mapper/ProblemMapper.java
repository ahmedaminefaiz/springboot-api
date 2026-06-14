package org.urban.alert.service.mapper;

import org.mapstruct.*;
import org.urban.alert.dto.problem.*;
import org.urban.alert.entity.Problem;
import org.urban.alert.entity.ProblemStatusHistory;
import org.urban.alert.entity.Alert;
import org.urban.alert.entity.User;

import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProblemMapper {

    // ========== Entity to Response DTO ==========

    @Mapping(source = "user", target = "createdBy")
    @Mapping(source = "assignedTo", target = "assignedTo")
    @Mapping(source = "alerts", target = "alerts")
    @Mapping(source = "statusHistory", target = "statusHistory")
    ProblemResponseDTO entityToProblemResponse(Problem problem);

    // ========== User to Summary ==========

    @Mapping(source = "id", target = "id")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "prenom", target = "firstName")
    @Mapping(source = "nom", target = "lastName")
    UserSummaryDTO userToSummary(User user);

    // ========== Alert to Summary ==========

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "status", target = "status", qualifiedByName = "alertStatusToString")
    @Mapping(source = "user.id", target = "userId")
    AlertSummaryDTO alertToSummary(Alert alert);

    // ========== StatusHistory to DTO ==========

    @Mapping(source = "previousStatus", target = "previousStatus", qualifiedByName = "statusToString")
    @Mapping(source = "newStatus", target = "newStatus", qualifiedByName = "statusToString")
    ProblemStatusHistoryDTO statusHistoryToDTO(ProblemStatusHistory history);

    // ========== Helper methods ==========

    @Named("statusToString")
    default String statusToString(Object status) {
        if (status == null) {
            return null;
        }
        return status.toString();
    }

    @Named("alertStatusToString")
    default String alertStatusToString(Object status) {
        if (status == null) {
            return null;
        }
        return status.toString();
    }
}