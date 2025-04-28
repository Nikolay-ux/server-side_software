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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ItemResponseDto>> getAllItems() {
        List<ItemResponseDto> items = itemRepository.findAll().stream()
                .map(DtoMapper::toItemDto)
                .toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemResponseDto> getItemById(@PathVariable Long id) {
        return itemRepository.findById(id)
                .map(DtoMapper::toItemDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemResponseDto> createItem(@RequestBody ItemRequestDto itemRequest) {
        Item item = DtoMapper.toItem(itemRequest);
        Item saved = itemRepository.save(item);
        return ResponseEntity.ok(DtoMapper.toItemDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemResponseDto> updateItem(@PathVariable Long id, @RequestBody ItemRequestDto itemRequest) {
        return itemRepository.findById(id)
                .map(item -> {
                    item.setShippingWeight(itemRequest.getShippingWeight());
                    item.setDescription(itemRequest.getDescription());
                    Item saved = itemRepository.save(item);
                    return ResponseEntity.ok(DtoMapper.toItemDto(saved));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (itemRepository.existsById(id)) {
            itemRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
