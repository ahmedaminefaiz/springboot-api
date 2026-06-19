package org.urban.alert.dto.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

import org.urban.alert.entity.enums.RoleEnum;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDTO {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?:\\+212|0)[67]\\d{8}$", message = "Invalid Moroccanphone number")
    private String phone;

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @NotNull
    private LocalDate dateNaissance;

    @NotBlank
    private String ville;

    @NotBlank
    private String password;

    @NotNull
    private RoleEnum role;

    private Long supervisorId;
}
