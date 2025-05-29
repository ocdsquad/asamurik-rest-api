package com.asamurik_rest_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:jwt.properties")
public class JwtConfig {
    private String secretKey;
    private Long timeExpiration;

    public String getSecretKey() {
        return secretKey;
    }

    @Value("${secret.key}")
    private void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Long getTimeExpiration() {
        return timeExpiration;
    }

    @Value("${time.expiration}")
    private void setTimeExpiration(Long timeExpiration) {
        this.timeExpiration = timeExpiration;
    }
}
