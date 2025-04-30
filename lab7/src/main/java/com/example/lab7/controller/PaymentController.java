package com.example.lab7.controller;

import com.example.lab7.model.dto.request.PaymentRequestDto;
import com.example.lab7.model.dto.response.DtoMapper;
import com.example.lab7.model.dto.response.PaymentResponseDto;
import com.example.lab7.model.entity.Payment;
import com.example.lab7.repository.OrderRepository;
import com.example.lab7.repository.PaymentRepository;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CurrentUserService currentUserService;

    private boolean hasRole(Jwt jwt, String role) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) return false;
        List<String> roles = (List<String>) realmAccess.get("roles");
        return roles.contains(role);
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments(@AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        List<PaymentResponseDto> payments = paymentRepository.findAll()
                .stream()
                .map(DtoMapper::toPaymentDto)
                .toList();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/my")
    @Transactional(readOnly = true)
    public ResponseEntity<List<PaymentResponseDto>> getMyPayments() {
        Long customerId = currentUserService.getCurrentCustomerId();
        if (customerId == null) {
            return ResponseEntity.notFound().build();
        }
        List<PaymentResponseDto> payments = paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getOrder().getCustomer().getId().equals(customerId))
                .map(DtoMapper::toPaymentDto)
                .toList();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Long id,
                                                             @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return paymentRepository.findById(id)
                .map(DtoMapper::toPaymentDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto paymentRequest,
                                                            @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        Payment payment = DtoMapper.toPayment(paymentRequest, orderRepository);
        Payment savedPayment = paymentRepository.save(payment);
        return ResponseEntity.ok(DtoMapper.toPaymentDto(savedPayment));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<PaymentResponseDto> updatePayment(@PathVariable Long id,
                                                            @RequestBody PaymentRequestDto paymentRequest,
                                                            @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return paymentRepository.findById(id)
                .map(payment -> {
                    payment.setAmount(paymentRequest.getAmount());
                    payment.setStatus(paymentRequest.getStatus());
                    payment.setPaymentType(paymentRequest.getPaymentType());

                    if (paymentRequest.getOrderId() != null) {
                        orderRepository.findById(paymentRequest.getOrderId())
                                .ifPresent(payment::setOrder);
                    }

                    Payment saved = paymentRepository.save(payment);
                    return ResponseEntity.ok(DtoMapper.toPaymentDto(saved));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deletePayment(@PathVariable Long id,
                                              @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        if (paymentRepository.existsById(id)) {
            paymentRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}