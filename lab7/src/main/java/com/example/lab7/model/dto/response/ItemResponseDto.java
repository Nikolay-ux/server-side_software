package com.example.lab7.model.dto.response;

import com.example.lab7.model.value.Weight;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponseDto {
    private Long id;
    private Weight shippingWeight;
    private String description;
}
