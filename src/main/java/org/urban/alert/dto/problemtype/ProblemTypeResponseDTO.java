package org.urban.alert.dto.problemtype;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemTypeResponseDTO {
    
    private Long id;
    private String name;
    private String icon; //blob
    private Long adminId;
    private String adminName;
}
