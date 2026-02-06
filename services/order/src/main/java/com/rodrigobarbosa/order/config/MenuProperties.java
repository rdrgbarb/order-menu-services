package com.rodrigobarbosa.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "menu")
public record MenuProperties(String baseUrl, long timeoutMs) {}
