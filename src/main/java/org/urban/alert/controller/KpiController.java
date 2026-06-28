package org.urban.alert.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.urban.alert.dto.kpi.KpiDashboardDTO;
import org.urban.alert.service.KpiService;

@RestController
@RequestMapping("/v1/kpis")
@RequiredArgsConstructor
public class KpiController {

    private final KpiService kpiService;

    /**
     * GET /v1/kpis/dashboard
     *
     * Paramètres optionnels :
     *   - periodDays     : fenêtre temporelle en jours (ex: 30 = dernier mois). Null = toutes les données.
     *   - agentId        : filtre les interventions par agent.
     *   - problemTypeId  : filtre par type de problème (category des alertes liées).
     *   - criticalityId  : filtre par criticité des problèmes.
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('SUPER_AGENT')")
    public ResponseEntity<KpiDashboardDTO> getDashboard(
            @RequestParam(required = false) Integer periodDays,
            @RequestParam(required = false) Long agentId,
            @RequestParam(required = false) Long problemTypeId,
            @RequestParam(required = false) Long criticalityId) {

        KpiDashboardDTO dashboard = kpiService.getDashboard(periodDays, agentId, problemTypeId, criticalityId);
        return ResponseEntity.ok(dashboard);
    }
}