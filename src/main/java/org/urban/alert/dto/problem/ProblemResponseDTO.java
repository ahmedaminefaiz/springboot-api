package org.urban.alert.dto.problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.urban.alert.dto.criticality.CriticalityResponseDTO;
import org.urban.alert.entity.enums.ProblemStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemResponseDTO {

    private Long id;
    private ProblemStatusEnum status;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    // Relations
    private CriticalityResponseDTO criticality;
    private UserSummaryDTO createdBy;
    private List<AlertSummaryDTO> alerts; // Alertes liées
    private List<ProblemStatusHistoryDTO> statusHistory; // Historique des statuts
}
