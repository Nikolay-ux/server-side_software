package com.example.lab7.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class Measurement {
    @Column
    private String name;

    @Column
    private String symbol;

    protected Measurement() {}

    protected Measurement(String name, String symbol){
        this.name = name;
        this.symbol = symbol;
    }
}
