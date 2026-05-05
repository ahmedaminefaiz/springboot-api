package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class VerifyPhoneRequest {

    @NotBlank
    @Pattern(regexp = "^(?:\\+212|0)[67]\\d{8}$", message = "Invalid Moroccan phone number")
    private String phone;

    @NotBlank
    @Size(min = 6, max = 6, message = "Code must be exactly 6 digits")
    private String code;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}