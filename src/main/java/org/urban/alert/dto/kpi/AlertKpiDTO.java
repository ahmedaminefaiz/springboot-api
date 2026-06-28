package org.urban.alert.dto.kpi;

public record AlertKpiDTO(
        Long total,
        Long enAttente,
        Long qualifiees,
        Double delaiMoyenQualificationHeures
) {}