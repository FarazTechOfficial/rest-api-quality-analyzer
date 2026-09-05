package com.research.analyzer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient reportRestClient(@Value("${report-service.url}") String reportServiceUrl) {
        return RestClient.builder()
                .baseUrl(reportServiceUrl)
                .build();
    }
}
