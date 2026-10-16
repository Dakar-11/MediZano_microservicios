package com.medizano.proveedor.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalFeignConfig {

    @Bean
    RequestInterceptor internalServiceTokenInterceptor(
            @Value("${security.internal-service-token:medizano_internal_mesh_secret_token_2026_xyz987}") String token) {
        return template -> template.header("X-Internal-Service-Token", token);
    }
}

