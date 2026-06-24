package org.urban.alert.dto.intervention;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.urban.alert.entity.enums.InterventionActionTypeEnum;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInterventionRequestDTO {

    @NotNull(message = "L'ID du problème est requis")
    private Long problemId;

    @NotNull(message = "L'ID de l'agent est requis")
    private Long agentId;

    @NotBlank(message = "La description est requise")
    @Size(max = 2000)
    private String description;

    @NotNull(message = "Le type d'action est requis")
    private InterventionActionTypeEnum actionType;

    @Size(max = 3, message = "Maximum 3 photos")
    private List<String> photos;
}
