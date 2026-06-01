package org.urban.alert.dto.problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertSummaryDTO {
    private Long id;
    private String title;
    private String status;
    private Long userId;
}
