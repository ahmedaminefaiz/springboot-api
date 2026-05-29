package org.urban.alert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPhoneRequest {

    @NotBlank
    @Pattern(regexp = "^(?:\\+212|0)[67]\\d{8}$", message = "Invalid Moroccan phone number")
    private String phone;

    @NotBlank
    @Size(min = 6, max = 6, message = "Code must be exactly 6 digits")
    private String code;
}