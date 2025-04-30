package com.example.lab7.controller;

import com.example.lab7.model.dto.request.OrderRequestDto;
import com.example.lab7.model.dto.response.DtoMapper;
import com.example.lab7.model.dto.response.OrderResponseDto;
import com.example.lab7.model.entity.Order;
import com.example.lab7.repository.CustomerRepository;
import com.example.lab7.repository.OrderRepository;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final CurrentUserService currentUserService;

    private boolean hasRole(Jwt jwt, String role) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) return false;
        List<String> roles = (List<String>) realmAccess.get("roles");
        return roles.contains(role);
    }

    @GetMapping
    @Transactional
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(@AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        List<OrderResponseDto> orders = orderRepository.findAll()
                .stream()
                .map(DtoMapper::toOrderDto)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/my")
    @Transactional
    public ResponseEntity<List<OrderResponseDto>> getMyOrders() {

        Long customerId = currentUserService.getCurrentCustomerId();
        if (customerId == null) {
            return ResponseEntity.notFound().build();
        }
        List<OrderResponseDto> orders = orderRepository.findAll().stream()
                .filter(order -> order.getCustomer().getId().equals(customerId))
                .map(DtoMapper::toOrderDto)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id,
                                                         @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return orderRepository.findById(id)
                .map(DtoMapper::toOrderDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequest,
                                                        @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        try {
            Order order = DtoMapper.toOrder(orderRequest, customerRepository);
            Order saved = orderRepository.save(order);
            return ResponseEntity.ok(DtoMapper.toOrderDto(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Возвращаем 400 если customer не найден
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long id,
                                                        @RequestBody OrderRequestDto orderRequest,
                                                        @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(orderRequest.getStatus());
                    order.setDate(orderRequest.getDate());
                    Order saved = orderRepository.save(order);
                    return ResponseEntity.ok(DtoMapper.toOrderDto(saved));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id,
                                            @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
