package org.urban.alert.dto.kpi;

public record AgentPerformanceDTO(
        Long agentId,
        String nom,
        String prenom,
        Long total,
        Long reussies,
        Long enCours
) {}