package org.urban.alert.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SimilarityClientConfig {

    @Value("${similarity.url}")
    private String similarityUrl;

    @Bean
    public WebClient similarityWebClient() {
        return WebClient.builder()
                .baseUrl(similarityUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
