package com.example.lab7.repository;

import com.example.lab7.model.entity.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.orders WHERE c.id = :id")
    @Transactional
    Optional<Customer> findByIdWithOrders(@Param("id") Long id);

    @Query("SELECT c.id FROM Customer c WHERE c.name = :name")
    Optional<Long> findCustomerIdByUsername(@Param("name") String name);

    Optional<Customer> findByKeycloakId(String keycloakId);
}
