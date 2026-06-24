package org.urban.alert.dto.criticality;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriticalityCreateDTO {

    @NotBlank(message = "Le nom de la criticité est obligatoire")
    private String name;

    @NotNull(message = "Le délai en heures est obligatoire")
    @Min(value = 1, message = "Le délai doit être d'au moins 1 heure")
    private Integer delayHours;
}