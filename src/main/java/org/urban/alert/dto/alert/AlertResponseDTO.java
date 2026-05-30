package org.urban.alert.dto.alert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.urban.alert.dto.UserSummaryResponseDTO;
import org.urban.alert.dto.problemtype.ProblemTypeResponseDTO;
import org.urban.alert.entity.enums.AlertStatusEnum;
import org.urban.alert.entity.enums.AlertPriorityEnum;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertResponseDTO {

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

    // Relations
    private UserSummaryResponseDTO user;

    private ProblemTypeResponseDTO category;

    private Long ticketId;
}
