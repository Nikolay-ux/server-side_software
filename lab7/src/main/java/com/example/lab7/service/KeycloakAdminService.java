//package com.example.lab7.service;
//
//import lombok.RequiredArgsConstructor;
//import org.keycloak.admin.client.Keycloak;
//import org.keycloak.representations.idm.UserRepresentation;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class KeycloakAdminService {
//
//    private final Keycloak keycloak;
//
//    @Value("${keycloak.realm}")
//    private String realm;
//
//    public boolean isUserExists(String userId) {
//        List<UserRepresentation> users = keycloak.realm(realm)
//                .users()
//                .search(userId);
//        return !users.isEmpty();
//    }
//}