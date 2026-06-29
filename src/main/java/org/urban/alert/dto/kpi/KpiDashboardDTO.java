package org.urban.alert.dto.kpi;

public record KpiDashboardDTO(
        AlertKpiDTO alerts,
        ProblemKpiDTO problems,
        InterventionKpiDTO interventions
) {}