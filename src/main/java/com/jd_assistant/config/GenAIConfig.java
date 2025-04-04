package com.jd_assistant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class GenAIConfig {
    @Value("${genai.api.key}")
    private String apiKey;

    @Bean
    public String genAiApiKey() {
        return apiKey;
    }
}
