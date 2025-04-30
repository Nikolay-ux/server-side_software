package com.example.lab7.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloakAdminClient(
            @Value("${keycloak.admin-client.server-url}") String serverUrl,
            @Value("${keycloak.admin-client.realm}") String realm,
            @Value("${keycloak.admin-client.client-id}") String clientId,
            @Value("${keycloak.admin-client.username}") String username,
            @Value("${keycloak.admin-client.password}") String password)
    {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .username(username)
                .password(password)
                .build();
    }

}
