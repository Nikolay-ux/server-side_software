package com.example.lab7.repository;

import com.example.lab7.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @Query(value = "INSERT INTO orders(date, status, customer_id) VALUES (:date, CAST(:status AS order_status), :customerId", nativeQuery = true)
    void insertOrder(@Param("date") LocalDateTime date,
                     @Param("status") String status,
                     @Param("customerId") Long customerId);
}
