package com.polarbookshop.edgeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

//le package org.springframework.web.server contient les classes et interfaces pour configurer la sécurité via la prog reéactive
@Configuration(proxyBeanMethods = false)
//@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    ServerOAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository() {
        //bean stockant le contenu de l'objet OAuth2AuthorizedClient (l'access Token) dans la session utilisateur Redis
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    //Sans configuration explicite, Spring Boot active l'authentification par formulaire et basique
    //définition du bean reactif SecurityWebFilterChain
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository)  {
        return http // permet de configurer la chaine
                //authentification non nécessaire pour obtenir la SPA et accéder aux livres
                .authorizeExchange(exchange-> exchange
                        .pathMatchers("/","/*.css","/*.js","/favicon.ico").permitAll()
                        .pathMatchers(HttpMethod.GET,"/books/**").permitAll()
                        .anyExchange().authenticated()
                )
                //si une exception du fait d'un accès à une ressource par un user non authentifié alors 401 retourné
                .exceptionHandling(exceptionHandling ->exceptionHandling
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .oauth2Login(Customizer.withDefaults())
                //configuration du logout dans la chaine de filtres Spring Security
                .logout(logout
                        ->logout.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
                )
                //retour d'un CSRFToken intégré au cookie de session
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new XorServerCsrfTokenRequestAttributeHandler()::handle))
                .build();// création du bean
    }

    //Configuration de la propagation du logout à Keycloak spécifiant la redirection vers la page d'accueil
    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) { //ReactiveClientRegistrationRepository permet d'accèder aux informations du(des) client(s) inscrit(s) configuré(s) dans application.yml
        //instanciation d'un gestionnaire de réussite de déconnexion du serveur d'autorisation (keycloak) initiée par un RP (oidc client)
        var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);

        //si réussite du logout redirection vers la page d'accueil
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");

        return oidcLogoutSuccessHandler;
    }

    //configuration d'un filtre qui souscrit juste au flux réactif d'émission du token CSRF
    //pour que le token soit émis dans le cadre réactif
    //contournement qui ne sera probablement plus nécessaire dans des versions supps.
    @Bean
    WebFilter csrfWebFilter() {
        return (exchange, chain) -> {
            exchange.getResponse().beforeCommit(
                    ()-> Mono.defer(
                            ()->{
                                Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
                                return csrfToken != null ? csrfToken.then() : Mono.empty();
                            }
                    )
            );
            return chain.filter(exchange);
        };
    }

}
