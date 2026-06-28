package org.urban.alert.dto.kpi;

public record ProblemKpiDTO(
        Long total,
        Long ouverts,
        Long resolus,
        Long sansInterventionActive,
        Double tempsMoyenResolutionHeures
) {}