package org.urban.alert.dto.criticality;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriticalityResponseDTO {
    private Long id;
    private String name;
    private Integer delayHours;
}