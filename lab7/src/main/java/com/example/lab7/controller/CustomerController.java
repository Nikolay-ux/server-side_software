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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {


    private final CustomerRepository customerRepository;
    private final CurrentUserService currentUserService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
        List<CustomerResponseDto> customers = customerRepository.findAll()
                .stream()
                .map(DtoMapper::toCustomerDto)
                .toList();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CustomerResponseDto> getMyCustomer() {
        Long customerId = currentUserService.getCurrentCustomerId();
        if (customerId == null) {
            return ResponseEntity.notFound().build();
        }
        return customerRepository.findById(customerId)
                .map(DtoMapper::toCustomerDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(DtoMapper::toCustomerDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody CustomerRequestDto customerRequest) {
        Customer customer = DtoMapper.toCustomer(customerRequest);
        Customer saved = customerRepository.save(customer);
        return ResponseEntity.ok(DtoMapper.toCustomerDto(saved));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CustomerResponseDto> updateMyCustomer(@RequestBody CustomerRequestDto customerRequest) {
        Long customerId = currentUserService.getCurrentCustomerId();
        if (customerId == null) {
            return ResponseEntity.notFound().build();
        }
        return customerRepository.findById(customerId)
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDto> updateCustomerById(@PathVariable Long id, @RequestBody CustomerRequestDto customerRequest) {
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
