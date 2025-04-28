package com.example.lab7.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "credit_payments")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Credit extends Payment {
    @Column
    private String number;

    @Column
    private String type;

    @Column(name = "exp_date")
    private LocalDateTime expDate;

    public Credit(float amount,
                  Order order,
                  PaymentStatus status,
                  String number,
                  String type,
                  LocalDateTime expDate) {
        super(amount, order, status, PaymentType.CREDIT);
        this.number = number;
        this.type = type;
        this.expDate = expDate;
    }
}
