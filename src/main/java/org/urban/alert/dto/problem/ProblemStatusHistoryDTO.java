package org.urban.alert.dto.problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemStatusHistoryDTO {
    private Long id;
    private String previousStatus;
    private String newStatus;
    private String changedBy;
    private String comment;
    private LocalDateTime changedAt;
}
