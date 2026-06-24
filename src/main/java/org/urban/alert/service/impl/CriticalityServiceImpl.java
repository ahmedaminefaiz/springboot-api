package org.urban.alert.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urban.alert.dto.criticality.CriticalityCreateDTO;
import org.urban.alert.dto.criticality.CriticalityResponseDTO;
import org.urban.alert.entity.Criticality;
import org.urban.alert.exception.CriticalityNotFoundException;
import org.urban.alert.repository.CriticalityRepository;
import org.urban.alert.repository.ProblemRepository;
import org.urban.alert.service.CriticalityService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CriticalityServiceImpl implements CriticalityService {

    private final CriticalityRepository criticalityRepository;
    private final ProblemRepository problemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CriticalityResponseDTO> getAllCriticalities() {
        log.info("Fetching all criticalities");
        return criticalityRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CriticalityResponseDTO getCriticalityById(Long id) {
        log.info("Fetching criticality with ID: {}", id);
        return toDTO(criticalityRepository.findById(id)
                .orElseThrow(() -> new CriticalityNotFoundException(id)));
    }

    @Override
    public CriticalityResponseDTO createCriticality(CriticalityCreateDTO request) {
        log.info("Creating criticality: {}", request.getName());
        Criticality criticality = Criticality.builder()
                .name(request.getName())
                .delayHours(request.getDelayHours())
                .build();
        return toDTO(criticalityRepository.save(criticality));
    }

    @Override
    public CriticalityResponseDTO updateCriticality(Long id, CriticalityCreateDTO request) {
        log.info("Updating criticality with ID: {}", id);
        Criticality criticality = criticalityRepository.findById(id)
                .orElseThrow(() -> new CriticalityNotFoundException(id));
        criticality.setName(request.getName());
        criticality.setDelayHours(request.getDelayHours());
        return toDTO(criticalityRepository.save(criticality));
    }

    @Override
    public void deleteCriticality(Long id) {
        log.info("Deleting criticality with ID: {}", id);
        if (!criticalityRepository.existsById(id)) {
            throw new CriticalityNotFoundException(id);
        }
        if (problemRepository.existsByCriticalityId(id)) {
            throw new IllegalStateException(
                    "Impossible de supprimer cette criticité : elle est utilisée par un ou plusieurs problèmes");
        }
        criticalityRepository.deleteById(id);
    }

    private CriticalityResponseDTO toDTO(Criticality c) {
        return CriticalityResponseDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .delayHours(c.getDelayHours())
                .build();
    }
}