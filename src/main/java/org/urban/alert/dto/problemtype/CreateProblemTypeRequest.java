package org.urban.alert.dto.problemtype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProblemTypeRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 255)
    private String icon;
}
