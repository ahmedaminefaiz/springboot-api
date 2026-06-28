package org.urban.alert.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urban.alert.dto.alert.AlertResponseDTO;
import org.urban.alert.dto.alert.AlertSimilarityResponseDTO;
import org.urban.alert.dto.alert.SimilarityCandidateDTO;
import org.urban.alert.dto.alert.SimilarityRequestDTO;
import org.urban.alert.dto.alert.SimilarityScoreDTO;
import org.urban.alert.entity.Alert;
import org.urban.alert.exception.alert.AlertNotFoundException;
import org.urban.alert.repository.AlertRepository;
import org.urban.alert.service.AlertSimilarityService;
import org.urban.alert.service.SimilarityClient;
import org.urban.alert.service.mapper.AlertMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AlertSimilarityServiceImpl implements AlertSimilarityService {

    private final AlertRepository alertRepository;
    private final SimilarityClient similarityClient;
    private final AlertMapper alertMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AlertSimilarityResponseDTO> findSimilarAlerts(Long alertId) {
        Alert source = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));

        if (source.getImages() == null || source.getImages().isEmpty()) {
            log.info("Alerte {} sans photo — similarité IA ignorée", alertId);
            return Collections.emptyList();
        }

        String sourceImageUrl = source.getImages().get(0);

        List<Alert> candidates = alertRepository.findSimilarAlerts(
                alertId,
                source.getCategory().getId(),
                source.getLatitude().doubleValue(),
                source.getLongitude().doubleValue(),
                300.0
        );

        List<Alert> candidatesWithImage = candidates.stream()
                .filter(a -> a.getImages() != null && !a.getImages().isEmpty())
                .toList();

        if (candidatesWithImage.isEmpty()) {
            log.info("Aucun candidat avec photo pour l'alerte {} — similarité IA ignorée", alertId);
            return Collections.emptyList();
        }

        Map<Long, Alert> candidatesMap = candidatesWithImage.stream()
                .collect(Collectors.toMap(Alert::getId, a -> a));

        List<SimilarityCandidateDTO> candidateDTOs = candidatesWithImage.stream()
                .map(a -> SimilarityCandidateDTO.builder()
                        .id(a.getId())
                        .imageUrl(a.getImages().get(0))
                        .build())
                .toList();

        SimilarityRequestDTO request = SimilarityRequestDTO.builder()
                .sourceImageUrl(sourceImageUrl)
                .candidates(candidateDTOs)
                .build();

        List<SimilarityScoreDTO> scores = similarityClient.getSimilarities(request);

        return scores.stream()
                .map(scoreDTO -> {
                    AlertResponseDTO alertDTO = alertMapper.entityToAlertResponse(
                            candidatesMap.get(scoreDTO.getId()));
                    return AlertSimilarityResponseDTO.builder()
                            .id(alertDTO.getId())
                            .title(alertDTO.getTitle())
                            .description(alertDTO.getDescription())
                            .latitude(alertDTO.getLatitude())
                            .longitude(alertDTO.getLongitude())
                            .address(alertDTO.getAddress())
                            .status(alertDTO.getStatus())
                            .priority(alertDTO.getPriority())
                            .isAnonymous(alertDTO.getIsAnonymous())
                            .images(alertDTO.getImages())
                            .videos(alertDTO.getVideos())
                            .createdAt(alertDTO.getCreatedAt())
                            .updatedAt(alertDTO.getUpdatedAt())
                            .user(alertDTO.getUser())
                            .category(alertDTO.getCategory())
                            .ticketId(alertDTO.getTicketId())
                            .score(scoreDTO.getScore())
                            .build();
                })
                .toList();
    }
}
