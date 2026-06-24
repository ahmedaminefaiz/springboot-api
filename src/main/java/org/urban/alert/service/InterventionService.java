package org.urban.alert.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.urban.alert.dto.intervention.CreateInterventionRequestDTO;
import org.urban.alert.dto.intervention.CreateInterventionUpdateRequestDTO;
import org.urban.alert.dto.intervention.InterventionResponseDTO;
import org.urban.alert.dto.intervention.InterventionUpdateResponseDTO;

import java.util.List;

public interface InterventionService {

    InterventionResponseDTO createIntervention(CreateInterventionRequestDTO request, Long superAgentId);

    Page<InterventionResponseDTO> getMyInterventions(Long agentId, Pageable pageable);

    Page<InterventionResponseDTO> getByProblem(Long problemId, Long userId, String userRole, Pageable pageable);

    InterventionResponseDTO getById(Long id, Long userId, String userRole);

    InterventionResponseDTO updateIntervention(Long id, CreateInterventionUpdateRequestDTO request, Long agentId);

    InterventionResponseDTO createInterventionUpdate(Long id, CreateInterventionUpdateRequestDTO request, Long agentId);

    List<InterventionUpdateResponseDTO> getInterventionUpdates(Long interventionId, Long userId, String userRole);

    InterventionResponseDTO addPhoto(Long id, String photoUrl, Long superAgentId);

    InterventionResponseDTO removePhoto(Long id, String photoUrl, Long superAgentId);

    InterventionResponseDTO closeIntervention(Long id, Long superAgentId);

    Long getCurrentUserId();
}
