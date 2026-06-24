package org.urban.alert.service.mapper;

import org.mapstruct.*;
import org.urban.alert.dto.intervention.InterventionResponseDTO;
import org.urban.alert.dto.intervention.InterventionUpdateResponseDTO;
import org.urban.alert.entity.Intervention;
import org.urban.alert.entity.InterventionUpdate;
import org.urban.alert.entity.enums.InterventionStatusEnum;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InterventionMapper {

    @Mapping(source = "problem.id", target = "problemId")
    @Mapping(source = "problem.title", target = "problemTitle")
    @Mapping(source = "agent.id", target = "agentId")
    @Mapping(target = "agentFullName", expression = "java(intervention.getAgent().getPrenom() + \" \" + intervention.getAgent().getNom())")
    InterventionResponseDTO entityToResponse(Intervention intervention);

    @Mapping(source = "intervention.id", target = "interventionId")
    @Mapping(target = "statusLabel", expression = "java(getStatusLabel(update.getStatus()))")
    InterventionUpdateResponseDTO updateEntityToResponse(InterventionUpdate update);

    default String getStatusLabel(InterventionStatusEnum status) {
        return switch (status) {
            case AFFECTEE -> "Affectée";
            case EN_COURS -> "En cours";
            case SUSPENDUE -> "Suspendue";
            case EN_ATTENTE_AUTRE_EQUIPE -> "En attente autre équipe";
            case RESOLUE -> "Résolue";
            case PARTIELLEMENT_RESOLUE -> "Partiellement résolue";
            case ECHEC_INTERVENTION -> "Échec intervention";
            case CLOTUREE -> "Clôturée";
        };
    }
}
