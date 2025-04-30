package com.example.lab7.controller;

import com.example.lab7.model.dto.request.OrderDetailRequestDto;
import com.example.lab7.model.dto.response.DtoMapper;
import com.example.lab7.model.dto.response.OrderDetailResponseDto;
import com.example.lab7.model.entity.OrderDetail;
import com.example.lab7.repository.ItemRepository;
import com.example.lab7.repository.OrderDetailRepository;
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
@RequestMapping("/api/order-details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final CurrentUserService currentUserService;
    private final ItemRepository itemRepository;

    private boolean hasRole(Jwt jwt, String role) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) return false;
        List<String> roles = (List<String>) realmAccess.get("roles");
        return roles.contains(role);
    }

    @GetMapping
    @Transactional
    public ResponseEntity<List<OrderDetailResponseDto>> getAllOrderDetails(@AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        List<OrderDetailResponseDto> details = orderDetailRepository.findAll()
                .stream()
                .map(DtoMapper::toOrderDetailDto)
                .toList();
        return ResponseEntity.ok(details);
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderDetailResponseDto>> getMyOrderDetails() {
        Long customerId = currentUserService.getCurrentCustomerId();
        if (customerId == null) {
            return ResponseEntity.notFound().build();
        }
        List<OrderDetailResponseDto> details = orderDetailRepository.findAll()
                .stream()
                .filter(detail -> detail.getOrder().getCustomer().getId().equals(customerId))
                .map(DtoMapper::toOrderDetailDto)
                .toList();
        return ResponseEntity.ok(details);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponseDto> getOrderDetailById(@PathVariable Long id,
                                                                     @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return orderDetailRepository.findById(id)
                .map(DtoMapper::toOrderDetailDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrderDetailResponseDto> createOrderDetail(@RequestBody OrderDetailRequestDto detailRequest,
                                                                    @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        OrderDetail detail = DtoMapper.toOrderDetail(detailRequest, orderRepository, itemRepository);
        OrderDetail saved = orderDetailRepository.save(detail);
        return ResponseEntity.ok(DtoMapper.toOrderDetailDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDetailResponseDto> updateOrderDetail(@PathVariable Long id,
                                                                    @RequestBody OrderDetailRequestDto detailRequest,
                                                                    @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return orderDetailRepository.findById(id)
                .map(detail -> {
                    detail.setQuantity(detailRequest.getQuantity());
                    detail.setTaxStatus(detailRequest.getTaxStatus());
                    OrderDetail saved = orderDetailRepository.save(detail);
                    return ResponseEntity.ok(DtoMapper.toOrderDetailDto(saved));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable Long id,
                                                  @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        if (orderDetailRepository.existsById(id)) {
            orderDetailRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
