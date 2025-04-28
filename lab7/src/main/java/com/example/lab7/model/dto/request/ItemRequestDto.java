package com.example.lab7.model.dto.request;

import com.example.lab7.model.value.Weight;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRequestDto {
    private Weight shippingWeight;
    private String description;
}
