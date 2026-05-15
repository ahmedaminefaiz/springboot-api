package org.urban.alert.dto.problemtype;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProblemTypeRequest {

    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 255)
    private String icon;
}
