package com.polarbookshop.edgeservice.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.security.Principal;

@Configuration
public class RateLimiterConfig {
    //définition d'un KeyResolver pour utiliser un seul bucket
    @Bean
    public KeyResolver keyResolver() {
        //retourne un bean KeyResolver
        //la clé est le nom utilisateur extrait du Principal dans le cas d'un utilisateur authentifié sinon "anonymous"
        return exchange -> exchange.getPrincipal()
                .map(Principal::getName)
                .defaultIfEmpty("anonymous");
    }
}
