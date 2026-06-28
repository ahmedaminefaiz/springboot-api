package org.urban.alert.dto.alert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.urban.alert.dto.problemtype.ProblemTypeResponseDTO;
import org.urban.alert.dto.usersummary.UserSummaryResponseDTO;
import org.urban.alert.entity.enums.AlertPriorityEnum;
import org.urban.alert.entity.enums.AlertStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertSimilarityResponseDTO {

    private Long id;
    private String title;
    private String description;
    private Float latitude;
    private Float longitude;
    private String address;
    private AlertStatusEnum status;
    private AlertPriorityEnum priority;
    private Boolean isAnonymous;
    private List<String> images;
    private List<String> videos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserSummaryResponseDTO user;
    private ProblemTypeResponseDTO category;
    private Long ticketId;
    private Double score;
}
