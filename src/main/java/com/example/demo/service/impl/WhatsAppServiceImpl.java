package com.example.demo.service.impl;

import com.example.demo.service.WhatsAppService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class WhatsAppServiceImpl implements WhatsAppService {

    @Value("${whatsapp.api.url}")
    private String apiUrl;

    @Value("${whatsapp.phone.number.id}")
    private String phoneNumberId;

    @Value("${whatsapp.access.token}")
    private String accessToken;

    private final RestClient restClient;

    public WhatsAppServiceImpl(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Override
    public void sendOtp(String recipientPhone, String code) {
        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "to", normalizePhone(recipientPhone),
                "type", "text",
                "text", Map.of("body", "Your verification code is: " + code + ". It expires in 15 minutes.")
                );

        restClient.post()
                .uri(apiUrl + "/" + phoneNumberId + "/messages")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    private String normalizePhone(String phone) {
        if (phone.startsWith("+")) return phone.substring(1);
        if (phone.startsWith("0")) return "212" + phone.substring(1);
        return phone;
    }
}