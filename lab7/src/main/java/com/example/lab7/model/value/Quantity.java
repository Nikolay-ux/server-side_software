package com.example.lab7.model.value;

import com.example.lab7.model.entity.Measurement;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "quantity_name")),
        @AttributeOverride(name = "symbol", column = @Column(name = "quantity_symbol"))
})
public class Quantity extends Measurement {
    @Column
    private int value;

    public Quantity(int value, String name, String symbol) {
        super(name, symbol);
        this.value = value;
    }
}
