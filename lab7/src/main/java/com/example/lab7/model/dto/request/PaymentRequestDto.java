package com.example.lab7.model.dto.request;

import com.example.lab7.model.entity.PaymentStatus;
import com.example.lab7.model.entity.PaymentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentRequestDto {
    private float amount;
    private Long orderId;
    private PaymentStatus status;
    private PaymentType paymentType;

    // Поля для Cash
    private Float cashTendered;

    // Поля для Check
    private String name;
    private String bankID;

    // Поля для Credit
    private String number;
    private String type;
    private LocalDateTime expDate;
}