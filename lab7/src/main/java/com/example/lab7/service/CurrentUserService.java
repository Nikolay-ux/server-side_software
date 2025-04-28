package com.example.lab7.service;

import com.example.lab7.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final CustomerRepository customerRepository;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }

    public Long getCurrentCustomerId() {
        String username = getCurrentUsername();
        if (username == null) {
            return null;
        }
        return customerRepository.findCustomerIdByUsername(username).orElse(null);
    }
}
