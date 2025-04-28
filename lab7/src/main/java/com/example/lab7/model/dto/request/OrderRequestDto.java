package com.example.lab7.model.dto.request;

import com.example.lab7.model.entity.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderRequestDto {
    private LocalDateTime date;
    private OrderStatus status;
    private Long customerId;
}
