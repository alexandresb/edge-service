package com.polarbookshop.edgeservice.web;

import com.polarbookshop.edgeservice.user.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class UserController {


    @GetMapping("user")
    public Mono<User> getUser(@AuthenticationPrincipal OidcUser oidcUser) { //injection d'OidcUser contenant les informations de l'utilisateur authentifié (principal)
        var user = new User(oidcUser.getPreferredUsername(),
                oidcUser.getGivenName(),
                oidcUser.getFamilyName(),
                oidcUser.getClaimAsStringList("roles"));//récup des rôles depuls la claim roles intégrée à l'id token
        return Mono.just(user);
    }
}
