package com.polarbookshop.edgeservice.config;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;



@WebFluxTest
@Import(SecurityConfig.class)
public class SecurityConfigTests {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
   void whenLogoutNotAuthenticatedAndNoCsrfTokenThen403(){
        webTestClient.post().uri("/logout").exchange().expectStatus().isForbidden();
    }

    @Test
    void whenLogoutAuthenticatedAndWithCsrfTokenThen302(){
        Mockito.when(clientRegistrationRepository.findByRegistrationId("test"))
                        .thenReturn(Mono.just(testClientRegistration()));
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockOidcLogin()) //mute la requête pour spécifier un user authentifié
                .mutateWith(SecurityMockServerConfigurers.csrf()) //mute la requête pour intégrer le token CSRF
                .post()
                .uri("/logout")
                .exchange()
                .expectStatus().isFound();
    }

    //définition d'un ClientRegistration intégrant une simulation d'un serveur d'autorisation
    private ClientRegistration testClientRegistration(){
        return ClientRegistration.withRegistrationId("test")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientId("test")
                .authorizationUri("https://sso.polarbookshop.com/auth")
                .tokenUri("https://sso.polarbookshop.com/token")
                .redirectUri("https://polarbookshop.com")
                .build();
    }
}
