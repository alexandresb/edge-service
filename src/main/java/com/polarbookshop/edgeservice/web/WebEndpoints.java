package com.polarbookshop.edgeservice.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
public class WebEndpoints {

    //définition de la route associant l'URI de fallback aux fonctions gestionnaires
    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        //retourne un bean fonctionnel RouterFunction (une fonction route) prenant en charge les requêtes GET et POST vers l'uri de fallback
        return RouterFunctions.route()
                .GET("/catalog-fallback", request -> ServerResponse.ok().body(Mono.just(""), String.class))
                .POST("/catalog-fallback", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build())
                .build();
    }
}
