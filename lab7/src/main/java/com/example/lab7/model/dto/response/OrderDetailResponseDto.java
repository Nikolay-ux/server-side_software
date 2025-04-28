package com.example.lab7.model.dto.response;

import lombok.Data;

@Data
public class OrderDetailResponseDto {
    private Long id;
    private String itemName;
    private int quantity;
    private String quantityName;
    private String quantitySymbol;
}
