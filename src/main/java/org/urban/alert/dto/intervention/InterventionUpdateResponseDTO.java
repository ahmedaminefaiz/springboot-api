package org.urban.alert.dto.intervention;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.urban.alert.entity.enums.InterventionStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterventionUpdateResponseDTO {

    private Long id;
    private Long interventionId;
    private String rapport;
    private InterventionStatusEnum status;
    private String statusLabel;
    private List<String> photos;
    private LocalDateTime createdAt;
}
