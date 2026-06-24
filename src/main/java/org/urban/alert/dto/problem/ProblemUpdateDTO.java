package org.urban.alert.dto.problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemUpdateDTO {

    private String title;

    private String description;

    private Long criticalityId;

    private List<Long> addAlertIds;
    private List<Long> removeAlertIds;
}
