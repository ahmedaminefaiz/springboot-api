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
public class UpdateAlertRequestDTO {

    @Size(min = 3, max = 255, message = "Le titre doit contenir entre 3 et 255 caractères")
    private String title;

    @Size(min = 10, max = 5000, message = "La description doit contenir entre 10 et 5000 caractères")
    private String description;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Float latitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Float longitude;

    @Size(max = 500, message = "L'adresse ne peut pas dépasser 500 caractères")
    private String address;

    private Long categoryId;

    private AlertPriorityEnum priority;

    private Boolean isAnonymous;
}
