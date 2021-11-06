package com.scalable.capital.exchange.rate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OutgoingSecurityConfig {

    @Bean("webClient")
    public WebClient getWebClient() {
        return WebClient.builder()
                .build();
    }

}
