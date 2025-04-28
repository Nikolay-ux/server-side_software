package com.example.lab7.model.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponseDto {
    private Long id;
    private float amount;
    private String status;
    private String paymentType;

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