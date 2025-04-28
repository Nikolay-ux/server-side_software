package com.example.lab7.model.dto.request;

import com.example.lab7.model.entity.OrderStatus;
import com.example.lab7.model.value.Quantity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDetailRequestDto {
    private Quantity quantity;
    private String taxStatus;
    private Long orderId;
    private Long itemId;
}
