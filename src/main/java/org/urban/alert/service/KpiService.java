package org.urban.alert.service;

import org.urban.alert.dto.kpi.KpiDashboardDTO;

public interface KpiService {

    KpiDashboardDTO getDashboard(Integer periodDays, Long agentId, Long problemTypeId, Long criticalityId);
}