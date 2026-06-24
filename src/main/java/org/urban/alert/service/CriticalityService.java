package org.urban.alert.service;

import org.urban.alert.dto.criticality.CriticalityCreateDTO;
import org.urban.alert.dto.criticality.CriticalityResponseDTO;

import java.util.List;

public interface CriticalityService {

    List<CriticalityResponseDTO> getAllCriticalities();

    CriticalityResponseDTO getCriticalityById(Long id);

    CriticalityResponseDTO createCriticality(CriticalityCreateDTO request);

    CriticalityResponseDTO updateCriticality(Long id, CriticalityCreateDTO request);

    void deleteCriticality(Long id);
}