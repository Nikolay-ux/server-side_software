package com.example.lab7.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "check_payments")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Check extends Payment {
    @Column
    private String name;

    @Column(name = "bank_id")
    private String bankID;

    public Check(float amount,
                 Order order,
                 PaymentStatus status,
                 String name,
                 String bankID) {
        super(amount, order, status, PaymentType.CHECK);
        this.name = name;
        this.bankID = bankID;
    }
}
