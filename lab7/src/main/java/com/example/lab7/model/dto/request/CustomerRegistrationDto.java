package com.example.lab7.model.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CustomerRegistrationDto {
    @NotBlank(message = "Name is mandatory")
    private String name;

    private String city;
    private String street;
    private String zipcode;
}