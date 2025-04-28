package com.example.lab7.model.dto;


import com.example.lab7.model.entity.OrderStatus;
import com.example.lab7.model.entity.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderFilterDto {
    private String customerName;
    private String deliveryAddress;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String paymentType;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
}
