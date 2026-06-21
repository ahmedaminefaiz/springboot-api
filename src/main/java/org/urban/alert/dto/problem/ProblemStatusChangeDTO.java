package org.urban.alert.dto.problem;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.urban.alert.entity.enums.ProblemStatusEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemStatusChangeDTO {

    @NotNull(message = "Le nouveau statut est requis")
    private ProblemStatusEnum newStatus;

    private String comment; // Raison du changement (optionnel)
}
