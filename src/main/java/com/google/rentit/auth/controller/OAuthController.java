package com.google.rentit.auth.controller;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class OAuthController {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuthController(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @GetMapping("/oauth2/authorize/google")
    public RedirectView authorizeGoogle() {
        ClientRegistration googleRegistration = clientRegistrationRepository.findByRegistrationId("google");
        String authorizationUrl = googleRegistration.getProviderDetails().getAuthorizationUri() + "?client_id=" + googleRegistration.getClientId() +
                "&redirect_uri=" + googleRegistration.getRedirectUri() + "&response_type=code&scope=" + googleRegistration.getScopes();
        return new RedirectView(authorizationUrl);
    }
}

