package com.example.lab7.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CustomerResponseDto {
    private String name;
    private String city;
    private String street;
    private String zipcode;
    private List<OrderResponseDto> orders;
}
