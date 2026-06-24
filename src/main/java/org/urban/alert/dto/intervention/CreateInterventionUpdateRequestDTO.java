package org.urban.alert.dto.intervention;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CreateInterventionUpdateRequestDTO {

    @NotBlank(message = "Le rapport est obligatoire")
    @Size(max = 5000)
    private String rapport;

    @NotNull(message = "Le statut est requis")
    private InterventionStatusEnum status;

    @NotNull(message = "La date de l'action est obligatoire")
    private LocalDateTime statusDate;

    @Size(max = 3, message = "Maximum 3 photos")
    private List<String> photos;
}
