package com.example.lab7.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cash_payments")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cash extends Payment {
    @Column(name = "cash_tendered")
    private float cashTendered;

    public Cash(float amount,
                Order order,
                PaymentStatus status,
                float cashTendered) {
        super(amount, order, status, PaymentType.CASH);
        this.cashTendered = cashTendered;
    }
}
