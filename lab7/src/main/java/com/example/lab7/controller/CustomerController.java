package com.example.lab7.controller;

import com.example.lab7.model.dto.request.CustomerRequestDto;
import com.example.lab7.model.dto.response.CustomerResponseDto;
import com.example.lab7.model.dto.response.DtoMapper;
import com.example.lab7.model.entity.Customer;
import com.example.lab7.model.value.Address;
import com.example.lab7.repository.CustomerRepository;
import com.example.lab7.service.CurrentUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {


    private final CustomerRepository customerRepository;
    private final CurrentUserService currentUserService;

    private boolean hasRole(Jwt jwt, String role) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) return false;
        List<String> roles = (List<String>) realmAccess.get("roles");
        return roles.contains(role);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers(@AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        List<CustomerResponseDto> customers = customerRepository.findAll()
                .stream()
                .map(DtoMapper::toCustomerDto)
                .toList();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/me")
    @Transactional
    public ResponseEntity<CustomerResponseDto> getMyCustomer(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        return customerRepository.findByKeycloakId(keycloakId)
                .map(DtoMapper::toCustomerDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable Long id,
                                                               @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        return customerRepository.findById(id)
                .map(DtoMapper::toCustomerDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody CustomerRequestDto customerRequest,
                                                              @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        Customer customer = DtoMapper.toCustomer(customerRequest);
        Customer saved = customerRepository.save(customer);
        return ResponseEntity.ok(DtoMapper.toCustomerDto(saved));
    }

    @PutMapping("/me")
    public ResponseEntity<CustomerResponseDto> updateMyCustomer(@RequestBody CustomerRequestDto customerRequest,
                                                                @AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();

        return customerRepository.findByKeycloakId(keycloakId)
                .map(customer -> {
                    customer.setName(customerRequest.getName());
                    customer.setAddress(new Address(
                            customerRequest.getCity(),
                            customerRequest.getStreet(),
                            customerRequest.getZipcode()
                    ));
                    Customer saved = customerRepository.save(customer);
                    return ResponseEntity.ok(DtoMapper.toCustomerDto(saved));
                }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> updateCustomerById(@PathVariable Long id,
                                                                  @RequestBody CustomerRequestDto customerRequest,
                                                                  @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setName(customerRequest.getName());
                    customer.setAddress(new Address(
                            customerRequest.getCity(),
                            customerRequest.getStreet(),
                            customerRequest.getZipcode()
                    ));
                    Customer saved = customerRepository.save(customer);
                    return ResponseEntity.ok(DtoMapper.toCustomerDto(saved));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id,
                                               @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
