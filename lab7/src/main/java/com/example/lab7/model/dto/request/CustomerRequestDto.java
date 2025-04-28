package com.example.lab7.model.dto.request;

import com.example.lab7.model.dto.response.OrderResponseDto;
import lombok.Data;

import java.util.List;

@Data
public class CustomerRequestDto {
    private String name;
    private String city;
    private String street;
    private String zipcode;
}
