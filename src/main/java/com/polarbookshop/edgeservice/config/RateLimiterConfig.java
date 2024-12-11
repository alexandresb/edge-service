package com.polarbookshop.edgeservice.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {
    //définition d'un KeyResolver pour utiliser un seul bucket
    @Bean
    public KeyResolver keyResolver() {
        //retourne un bean KeyResolver
        return exchange -> Mono.just("anonymous");
    }
}
