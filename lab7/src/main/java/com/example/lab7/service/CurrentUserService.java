package com.example.lab7.service;

import com.example.lab7.model.entity.Customer;
import com.example.lab7.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final CustomerRepository customerRepository;

    public String getCurrentKeycloakUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        return jwt.getSubject();
    }

    public Long getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) return null;

        String keycloakId = jwt.getSubject(); // используем sub
        return customerRepository.findByKeycloakId(keycloakId)
                .map(Customer::getId)
                .orElse(null);
    }
}

