package org.urban.alert.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.urban.alert.dto.alert.SimilarityRequestDTO;
import org.urban.alert.dto.alert.SimilarityResponseDTO;
import org.urban.alert.dto.alert.SimilarityScoreDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimilarityClient {

    private final WebClient similarityWebClient;

    public List<SimilarityScoreDTO> getSimilarities(SimilarityRequestDTO request) {
        log.info("Appel PFA_AI_MODEL /similarity — source={}, {} candidats",
                request.getSourceImageUrl(), request.getCandidates().size());
        return similarityWebClient
                .post()
                .uri("/similarity")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SimilarityResponseDTO.class)
                .map(SimilarityResponseDTO::getResults)
                .block();
    }
}
