package com.example.crudObsidiana.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // ConcurrentMapCacheManager: implementação in-memory nativa do Spring.
        // Não tem TTL automático, mas é zero-configuração e não exige nenhuma
        // dependência extra além do spring-boot-starter-cache.
        return new ConcurrentMapCacheManager("equipamentos", "equipamento");
    }

}
