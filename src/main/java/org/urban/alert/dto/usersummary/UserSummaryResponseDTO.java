package org.urban.alert.dto.usersummary;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponseDTO {
    private Long id;
    private String phone;
    private String nom;
    private String prenom;
    private String ville;
    private String role;
    private String status;
}
