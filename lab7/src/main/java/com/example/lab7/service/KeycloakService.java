package com.example.lab7.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public String createUser(String username, String password, String email) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);

        Response response = keycloak.realm(realm).users().create(user);
        if (response.getStatus() != 201) {
            String errorBody = response.readEntity(String.class);
            throw new RuntimeException("Failed to create user in Keycloak. Status: " + response.getStatus() + ", Body: " + errorBody);
        }

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(password);
        credentials.setTemporary(false);

        keycloak.realm(realm).users().get(userId).resetPassword(credentials);

        // Назначаем роль
        RoleRepresentation roleUser = keycloak.realm(realm).roles().get("ROLE_USER").toRepresentation();
        keycloak.realm(realm).users().get(userId).roles().realmLevel().add(List.of(roleUser));

        return userId;
    }
}
