package org.urban.alert.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.urban.alert.service.WhatsAppService;

import java.util.Map;

@Service
public class WhatsAppServiceImpl implements WhatsAppService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppServiceImpl.class);

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
        String normalizedPhone = normalizePhone(recipientPhone);
        log.info("Sending WhatsApp OTP to {} (normalized: {})", recipientPhone, normalizedPhone);

        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "to", normalizedPhone,
                "type", "text",
                "text", Map.of("body", "Your verification code is: " + code + ". It expires in 15 minutes.")
                );

        try {
            ResponseEntity<String> response = restClient.post()
                    .uri(apiUrl + "/" + phoneNumberId + "/messages")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toEntity(String.class);
            log.info("WhatsApp API response [{}]: {}", response.getStatusCode(), response.getBody());
        } catch (HttpClientErrorException e) {
            log.error("WhatsApp API error [{}]: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    @Override
    public void sendNotification(String recipientPhone, String message) {
        String normalizedPhone = normalizePhone(recipientPhone);
        log.info("Sending WhatsApp notification to {}", normalizedPhone);

        Map<String, Object> body = Map.of(
                "messaging_product", "whatsapp",
                "to", normalizedPhone,
                "type", "text",
                "text", Map.of("body", message)
        );

        try {
            ResponseEntity<String> response = restClient.post()
                    .uri(apiUrl + "/" + phoneNumberId + "/messages")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toEntity(String.class);
            log.info("WhatsApp notification sent [{}]", response.getStatusCode());
        } catch (HttpClientErrorException e) {
            log.error("WhatsApp notification error [{}]: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    private String normalizePhone(String phone) {
        if (phone.startsWith("+")) return phone.substring(1);
        if (phone.startsWith("0")) return "212" + phone.substring(1);
        return phone;
    }
}