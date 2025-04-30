package com.example.lab7.controller;

import com.example.lab7.model.dto.request.ItemRequestDto;
import com.example.lab7.model.dto.response.DtoMapper;
import com.example.lab7.model.dto.response.ItemResponseDto;
import com.example.lab7.model.entity.Item;
import com.example.lab7.repository.ItemRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;

    private boolean hasRole(Jwt jwt, String role) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) return false;
        List<String> roles = (List<String>) realmAccess.get("roles");
        return roles.contains(role);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllItems(@AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        List<ItemResponseDto> items = itemRepository.findAll().stream()
                .map(DtoMapper::toItemDto)
                .toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> getItemById(@PathVariable Long id,
                                                       @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        return itemRepository.findById(id)
                .map(DtoMapper::toItemDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@RequestBody ItemRequestDto itemRequest,
                                                      @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        Item item = DtoMapper.toItem(itemRequest);
        Item saved = itemRepository.save(item);
        return ResponseEntity.ok(DtoMapper.toItemDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDto> updateItem(@PathVariable Long id,
                                                      @RequestBody ItemRequestDto itemRequest,
                                                      @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return itemRepository.findById(id)
                .map(item -> {
                    item.setShippingWeight(itemRequest.getShippingWeight());
                    item.setDescription(itemRequest.getDescription());
                    Item saved = itemRepository.save(item);
                    return ResponseEntity.ok(DtoMapper.toItemDto(saved));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id,
                                           @AuthenticationPrincipal Jwt jwt) {
        if (!hasRole(jwt, "ROLE_ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
