package org.urban.alert.service;

import org.urban.alert.dto.alert.AlertSimilarityResponseDTO;

import java.util.List;

public interface AlertSimilarityService {

    List<AlertSimilarityResponseDTO> findSimilarAlerts(Long alertId);
}
