package org.urban.alert.dto.problem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemUpdateDTO {

    private String title;

    private String description;

    private List<Long> addAlertIds; // Ajouter des alertes
    private List<Long> removeAlertIds; // Retirer des alertes
}
