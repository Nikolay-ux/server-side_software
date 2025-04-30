package com.example.lab7.config;

import com.example.lab7.model.dto.request.CustomerRegistrationDto;
import com.example.lab7.model.dto.request.RegistrationRequestDto;
import com.example.lab7.model.dto.response.CustomerResponseDto;
import com.example.lab7.model.dto.response.DtoMapper;
import com.example.lab7.model.entity.Customer;
import com.example.lab7.model.value.Address;
import com.example.lab7.repository.CustomerRepository;
//import com.example.lab7.service.KeycloakAdminService;
import com.example.lab7.service.KeycloakService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakService keycloakService;
    private final CustomerRepository customerRepository;

    @PostMapping("/register")
    public ResponseEntity<CustomerResponseDto> register(@Valid @RequestBody RegistrationRequestDto dto) {
        String keycloakId = keycloakService.createUser(dto.getUsername(), dto.getPassword(), dto.getEmail());

        Customer customer = new Customer();
        customer.setName(dto.getName());
        customer.setKeycloakId(keycloakId);
        customer.setAddress(new Address(dto.getCity(), dto.getStreet(), dto.getZipcode()));

        Customer saved = customerRepository.save(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toCustomerDto(saved));
    }
}

