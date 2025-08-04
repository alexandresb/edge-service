package com.polarbookshop.edgeservice.user;

import com.polarbookshop.edgeservice.config.SecurityConfig;
import com.polarbookshop.edgeservice.web.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest(controllers = UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTests {
    @Autowired
    WebTestClient webTestClient;

    @MockBean
    ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    void whenNotAuthenticatedThen401() {
        webTestClient.get().uri("/user").exchange().expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedThenReturnUser() {
        var expectedUser = new User("john", "John", "Snow", List.of("employee", "customer"));
        webTestClient
                .mutateWith(configureMockOidcLogin(expectedUser))
                .get().uri("/user").exchange().expectStatus().is2xxSuccessful()
                .expectBody(User.class).value(user ->assertThat(user).isEqualTo(expectedUser));
    }

    private SecurityMockServerConfigurers.OidcLoginMutator configureMockOidcLogin(User  user) {
        //return une objet modifiant le contexte de la requête pour y assigner un utilisateur authentifié s'appuyant sur l'id token fabriqué
        return SecurityMockServerConfigurers.mockOidcLogin().idToken(//prend en param un consumer fonctionnel
                builder -> {
                    builder.claim(StandardClaimNames.PREFERRED_USERNAME, user.username());
                    builder.claim(StandardClaimNames.GIVEN_NAME, user.firstName());
                    builder.claim(StandardClaimNames.FAMILY_NAME, user.lastName());
                    builder.claim("roles", user.roles());
                }
        );
    }

}
