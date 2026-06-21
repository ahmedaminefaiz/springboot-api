package org.urban.alert.dto.alert;

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
public class AddMediaRequestDTO {

    @NotBlank(message = "L'URL du média est requise")
    @Size(max = 2048, message = "L'URL ne peut pas dépasser 2048 caractères")
    private String mediaUrl;
}
