package org.urban.alert.dto.intervention;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.urban.alert.entity.enums.InterventionActionTypeEnum;
import org.urban.alert.entity.enums.InterventionStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterventionResponseDTO {

    private Long id;
    private Long problemId;
    private String problemTitle;
    private Long agentId;
    private String agentFullName;
    private String description;
    private InterventionStatusEnum status;
    private InterventionActionTypeEnum actionType;
    private LocalDateTime interventionDate;
    private Integer duration;
    private List<String> photos;
}
