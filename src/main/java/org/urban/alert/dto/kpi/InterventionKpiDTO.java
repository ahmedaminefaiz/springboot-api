package org.urban.alert.dto.kpi;

import java.util.List;
import java.util.Map;

public record InterventionKpiDTO(
        Long total,
        Map<String, Long> parStatut,
        Double tauxReussite,
        Double tauxEchec,
        Double dureeMoyenneMinutes,
        Long enRetard,
        List<AgentPerformanceDTO> parAgent
) {}