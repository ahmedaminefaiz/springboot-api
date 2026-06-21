package org.urban.alert.dto.alert;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.urban.alert.entity.enums.AlertPriorityEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAlertRequestDTO {

    @NotBlank(message = "Le titre de l'alerte est requis")
    @Size(min = 3, max = 255, message = "Le titre doit contenir entre 3 et 255 caractères")
    private String title;

    @NotBlank(message = "La description est requise")
    @Size(min = 10, max = 5000, message = "La description doit contenir entre 10 et 5000 caractères")
    private String description;

    @NotNull(message = "La latitude est requise")
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Float latitude;

    @NotNull(message = "La longitude est requise")
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Float longitude;

    @Size(max = 500, message = "L'adresse ne peut pas dépasser 500 caractères")
    private String address;

    @NotNull(message = "La catégorie est requise")
    private Long categoryId;

    @NotNull(message = "La priorité est requise")
    private AlertPriorityEnum priority;

    @Builder.Default
    private Boolean isAnonymous = false;
}
