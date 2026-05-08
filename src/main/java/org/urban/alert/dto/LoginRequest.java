package org.urban.alert.dto;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@AllArgsConstructor
public class LoginRequest {
    private String phone;
    private String password;
}
