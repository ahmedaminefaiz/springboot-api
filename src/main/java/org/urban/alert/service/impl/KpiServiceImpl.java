package org.urban.alert.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urban.alert.config.CacheConfig;
import org.urban.alert.dto.kpi.*;
import org.urban.alert.entity.enums.InterventionStatusEnum;
import org.urban.alert.repository.kpi.AlertKpiRepository;
import org.urban.alert.repository.kpi.InterventionKpiRepository;
import org.urban.alert.repository.kpi.ProblemKpiRepository;
import org.urban.alert.service.KpiService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class KpiServiceImpl implements KpiService {

    private final AlertKpiRepository alertKpiRepository;
    private final ProblemKpiRepository problemKpiRepository;
    private final InterventionKpiRepository interventionKpiRepository;

    // Sentinelles : évitent les "? IS NULL" qui causent des erreurs de type PostgreSQL
    private static final LocalDateTime DATE_EPOCH = LocalDateTime.of(1900, 1, 1, 0, 0);
    private static final long NO_ID_FILTER = -1L;

    private static final List<InterventionStatusEnum> STATUSES_TERMINEES = List.of(
            InterventionStatusEnum.RESOLUE,
            InterventionStatusEnum.CLOTUREE,
            InterventionStatusEnum.ECHEC_INTERVENTION
    );
    private static final List<InterventionStatusEnum> STATUSES_REUSSIES = List.of(
            InterventionStatusEnum.RESOLUE,
            InterventionStatusEnum.CLOTUREE
    );

    @Override
    @Cacheable(value = CacheConfig.KPI_DASHBOARD_CACHE,
               key = "#periodDays + '-' + #agentId + '-' + #problemTypeId + '-' + #criticalityId")
    public KpiDashboardDTO getDashboard(Integer periodDays, Long agentId, Long problemTypeId, Long criticalityId) {
        log.info("Building KPI dashboard — periodDays={} agentId={} problemTypeId={} criticalityId={}",
                periodDays, agentId, problemTypeId, criticalityId);

        LocalDateTime since   = periodDays    != null ? LocalDateTime.now().minusDays(periodDays) : DATE_EPOCH;
        long          aId     = agentId       != null ? agentId       : NO_ID_FILTER;
        long          ptId    = problemTypeId != null ? problemTypeId : NO_ID_FILTER;
        long          cId     = criticalityId != null ? criticalityId : NO_ID_FILTER;

        return new KpiDashboardDTO(
                buildAlertKpi(since, ptId),
                buildProblemKpi(since, ptId, cId),
                buildInterventionKpi(since, aId, cId)
        );
    }

    private AlertKpiDTO buildAlertKpi(LocalDateTime since, long problemTypeId) {
        return new AlertKpiDTO(
                alertKpiRepository.countTotal(since, problemTypeId),
                alertKpiRepository.countEnAttente(since, problemTypeId),
                alertKpiRepository.countQualifiees(since, problemTypeId),
                alertKpiRepository.avgDelaiQualificationHeures(since, problemTypeId)
        );
    }

    private ProblemKpiDTO buildProblemKpi(LocalDateTime since, long problemTypeId, long criticalityId) {
        return new ProblemKpiDTO(
                problemKpiRepository.countTotal(since, criticalityId, problemTypeId),
                problemKpiRepository.countOuverts(since, criticalityId, problemTypeId),
                problemKpiRepository.countResolus(since, criticalityId, problemTypeId),
                problemKpiRepository.countSansInterventionActive(since, criticalityId, problemTypeId),
                problemKpiRepository.avgTempsMoyenResolutionHeures(since, criticalityId, problemTypeId)
        );
    }

    private InterventionKpiDTO buildInterventionKpi(LocalDateTime since, long agentId, long criticalityId) {
        Long total     = interventionKpiRepository.countTotal(since, agentId, criticalityId);
        Long terminees = interventionKpiRepository.countTerminees(since, agentId, criticalityId, STATUSES_TERMINEES);
        Long reussies  = interventionKpiRepository.countReussies(since, agentId, criticalityId, STATUSES_REUSSIES);
        Long echecs    = interventionKpiRepository.countEchecs(since, agentId, criticalityId, InterventionStatusEnum.ECHEC_INTERVENTION);

        double tauxReussite = terminees > 0 ? (double) reussies / terminees * 100 : 0.0;
        double tauxEchec    = terminees > 0 ? (double) echecs   / terminees * 100 : 0.0;

        Map<String, Long> parStatut = buildParStatut(
                interventionKpiRepository.countParStatut(since, agentId, criticalityId));

        List<AgentPerformanceDTO> parAgent = buildParAgent(
                interventionKpiRepository.performanceParAgent(since, criticalityId));

        return new InterventionKpiDTO(
                total,
                parStatut,
                Math.round(tauxReussite * 10.0) / 10.0,
                Math.round(tauxEchec    * 10.0) / 10.0,
                interventionKpiRepository.avgDureeMinutes(since, agentId, criticalityId),
                interventionKpiRepository.countEnRetard(since, agentId, criticalityId),
                parAgent
        );
    }

    private Map<String, Long> buildParStatut(List<Object[]> rows) {
        Map<String, Long> result = new HashMap<>();
        for (Object[] row : rows) {
            result.put(row[0].toString(), ((Number) row[1]).longValue());
        }
        return result;
    }

    private List<AgentPerformanceDTO> buildParAgent(List<Object[]> rows) {
        return rows.stream()
                .map(row -> new AgentPerformanceDTO(
                        ((Number) row[0]).longValue(),
                        (String)  row[1],
                        (String)  row[2],
                        ((Number) row[3]).longValue(),
                        ((Number) row[4]).longValue(),
                        ((Number) row[5]).longValue()
                ))
                .toList();
    }
}