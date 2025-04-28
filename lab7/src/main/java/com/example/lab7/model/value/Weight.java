package com.example.lab7.model.value;

import com.example.lab7.model.entity.Measurement;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
public class Weight extends Measurement {
    @Column(nullable = false)
    private BigDecimal value;

    public Weight() {}

    public Weight(BigDecimal value) {
        this.value = value;
    }

    public Weight(BigDecimal value, String name, String symbol) {
        super(name, symbol);
        this.value = value;
    }
}
