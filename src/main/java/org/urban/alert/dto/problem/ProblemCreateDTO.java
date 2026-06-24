package org.urban.alert.dto.problem;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemCreateDTO {

    private String title;

    private String description;

    @NotNull(message = "La criticité est obligatoire")
    private Long criticalityId;

    @NotNull(message = "Au moins une alerte doit être spécifiée")
    private List<Long> alertIds;
}

// ========== RESPONSE ==========

// ========== UPDATE REQUEST ==========

// ========== STATUS CHANGE REQUEST ==========

// ========== RELATED DTOs ==========

