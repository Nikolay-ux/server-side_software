package com.example.lab7.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDto {
    private Long id;
    private LocalDateTime date;
    private String status;
    private List<PaymentResponseDto> payments;
    private List<OrderDetailResponseDto> orderDetails;
}
