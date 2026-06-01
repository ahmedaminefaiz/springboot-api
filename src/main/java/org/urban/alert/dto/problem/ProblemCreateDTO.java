package org.urban.alert.dto.problem;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// ========== CREATE REQUEST ==========

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemCreateDTO {

    private String title;

    private String description;

    @NotNull(message = "L'ID de l'agent assigné est requis")
    private Long assignedToId; // ID de l'Agent

    @NotNull(message = "Au moins une alerte doit être spécifiée")
    private List<Long> alertIds; // IDs des alertes à assigner
}

// ========== RESPONSE ==========

// ========== UPDATE REQUEST ==========

// ========== STATUS CHANGE REQUEST ==========

// ========== RELATED DTOs ==========

