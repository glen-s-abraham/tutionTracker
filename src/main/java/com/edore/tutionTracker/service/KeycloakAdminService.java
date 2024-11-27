package com.edore.tutionTracker.service;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeycloakAdminService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public KeycloakAdminService(
            @Value("${keycloak.auth-server-url}") String authServerurl,
            @Value("${keycloak.resource}") String clientId,
            @Value("${keycloak.credentials.secret}") String clientSecret,
            @Value("${keycloak-admin.username}") String adminuserName,
            @Value("${keycloak-admin.password}") String adminPassword) {
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(authServerurl)
                .realm("master")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.PASSWORD)
                .username(adminuserName)
                .password(adminPassword)
                .build();
    }

}
