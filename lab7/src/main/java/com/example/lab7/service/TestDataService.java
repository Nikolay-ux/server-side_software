//package com.example.lab7.service;
//
//import com.example.lab7.model.entity.*;
//import com.example.lab7.model.value.Address;
//import com.example.lab7.model.value.Quantity;
//import com.example.lab7.model.value.Weight;
//import com.example.lab7.repository.*;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Profile("dev") // Только для dev-режима
//public class TestDataService {
//
//    private final CustomerRepository customerRepository;
//    private final ItemRepository itemRepository;
//    private final OrderRepository orderRepository;
//    private final PaymentRepository paymentRepository;
//    private final OrderDetailRepository orderDetailRepository;
//
//    @PostConstruct
//    public void initTestData() {
//        // Очистка старых данных
//        orderDetailRepository.deleteAll();
//        paymentRepository.deleteAll();
//        orderRepository.deleteAll();
//        itemRepository.deleteAll();
//        customerRepository.deleteAll();
//
//        // Создаем клиентов
//        Customer customer1 = new Customer(
//                "Иван Петров",
//                new Address("Москва", "ул. Ленина, 10", "101000"));
//
//        Customer customer2 = new Customer(
//                "Анна Сидорова",
//                new Address("Санкт-Петербург", "Невский пр., 25", "190000"));
//
//        customerRepository.saveAll(List.of(customer1, customer2));
//
//        // Создаем товары
//        Item item1 = new Item(
//                new Weight(new BigDecimal("100.50"), "weight", "kg"),
//                "Ноутбук");
//
//        Item item2 = new Item(
//                new Weight(new BigDecimal("25.75"), "weight", "kg"),
//                "Мышь");
//
//        itemRepository.saveAll(List.of(item1, item2));
//
//        // Создаем заказы с деталями и платежами
//        Order order1 = createOrderWithDetails(
//                LocalDateTime.now().minusDays(2),
//                OrderStatus.PROCESSING,
//                customer1,
//                item1,
//                1,
//                "STANDARD",
//                100.50f,
//                PaymentStatus.COMPLETED,
//                150.00f
//        );
//
//        Order order2 = createOrderWithDetails(
//                LocalDateTime.now().minusDays(1),
//                OrderStatus.DELIVERED,
//                customer2,
//                item2,
//                2,
//                "STANDARD",
//                25.75f,
//                PaymentStatus.PENDING,
//                "4111111111111111",
//                "VISA",
//                LocalDateTime.now().plusYears(2)
//        );
//
//        orderRepository.saveAll(List.of(order1, order2));
//    }
//
//    private Order createOrderWithDetails(
//            LocalDateTime date,
//            OrderStatus status,
//            Customer customer,
//            Item item,
//            int quantity,
//            String taxStatus,
//            float amount,
//            PaymentStatus paymentStatus,
//            float cashTendered) {
//
//        // Создаем заказ
//        Order order = new Order();
//        order.setDate(date);
//        order.setStatus(status);
//        order.setCustomer(customer);
//
//        // Создаем деталь заказа
//        OrderDetail detail = new OrderDetail(
//                new Quantity(quantity, "quantity", "pcs"),
//                taxStatus,
//                order
//        );
//        detail.setItem(item);
//        order.getOrderDetails().add(detail);
//
//        // Создаем платеж
//        Cash payment = new Cash(amount, order, paymentStatus, cashTendered);
//        order.getPayments().add(payment);
//
//        return order;
//    }
//
//    private Order createOrderWithDetails(
//            LocalDateTime date,
//            OrderStatus status,
//            Customer customer,
//            Item item,
//            int quantity,
//            String taxStatus,
//            float amount,
//            PaymentStatus paymentStatus,
//            String cardNumber,
//            String cardType,
//            LocalDateTime expDate) {
//
//        // Создаем заказ
//        Order order = new Order();
//        order.setDate(date);
//        order.setStatus(status);
//        order.setCustomer(customer);
//
//        // Создаем деталь заказа
//        OrderDetail detail = new OrderDetail(
//                new Quantity(quantity, "quantity", "pcs"),
//                taxStatus,
//                order
//        );
//        detail.setItem(item);
//        order.getOrderDetails().add(detail);
//
//        // Создаем платеж
//        Credit payment = new Credit(amount, order, paymentStatus,
//                cardNumber, cardType, expDate);
//        order.getPayments().add(payment);
//
//        return order;
//    }
//}