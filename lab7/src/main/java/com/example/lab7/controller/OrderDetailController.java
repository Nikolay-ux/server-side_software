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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/order-details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final CurrentUserService currentUserService;
    private final ItemRepository itemRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<List<OrderDetailResponseDto>> getAllOrderDetails() {
        List<OrderDetailResponseDto> details = orderDetailRepository.findAll()
                .stream()
                .map(DtoMapper::toOrderDetailDto)
                .toList();
        return ResponseEntity.ok(details);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDetailResponseDto> getOrderDetailById(@PathVariable Long id) {
        return orderDetailRepository.findById(id)
                .map(DtoMapper::toOrderDetailDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDetailResponseDto> createOrderDetail(@RequestBody OrderDetailRequestDto detailRequest) {
        OrderDetail detail = DtoMapper.toOrderDetail(detailRequest, orderRepository, itemRepository);
        OrderDetail saved = orderDetailRepository.save(detail);
        return ResponseEntity.ok(DtoMapper.toOrderDetailDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDetailResponseDto> updateOrderDetail(@PathVariable Long id, @RequestBody OrderDetailRequestDto detailRequest) {
        return orderDetailRepository.findById(id)
                .map(detail -> {
                    detail.setQuantity(detailRequest.getQuantity());
                    detail.setTaxStatus(detailRequest.getTaxStatus());
                    OrderDetail saved = orderDetailRepository.save(detail);
                    return ResponseEntity.ok(DtoMapper.toOrderDetailDto(saved));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable Long id) {
        if (orderDetailRepository.existsById(id)) {
            orderDetailRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
