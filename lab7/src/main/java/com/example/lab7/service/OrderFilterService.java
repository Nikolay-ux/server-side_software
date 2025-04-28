package com.example.lab7.service;

import com.example.lab7.model.dto.OrderFilterDto;
import com.example.lab7.model.entity.*;
import com.example.lab7.model.entity.Order;
import com.example.lab7.model.value.Address;
import com.example.lab7.repository.OrderRepository;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFilterService {
    private final OrderRepository orderRepository;

    public List<Order> findOrdersByFilter(OrderFilterDto filter) {
        return orderRepository.findAll(createSpecification(filter));
    }

    private Specification<Order> createSpecification(OrderFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Фильтр по имени клиента
            if (StringUtils.hasText(filter.getCustomerName())) {
                Join<Order, Customer> customerJoin = root.join("customer");
                predicates.add(cb.like(
                        cb.lower(customerJoin.get("name")),
                        "%" + filter.getCustomerName().toLowerCase() + "%"
                ));
            }

            // Фильтр по адресу доставки
            if (StringUtils.hasText(filter.getDeliveryAddress())) {
                Join<Order, Customer> customerJoin = root.join("customer");
                Join<Customer, Address> addressJoin = customerJoin.join("address"); // Добавляем join к Address

                predicates.add(cb.or(
                        cb.like(cb.lower(addressJoin.get("street")),
                                "%" + filter.getDeliveryAddress().toLowerCase() + "%"),
                        cb.like(cb.lower(addressJoin.get("city")),
                                "%" + filter.getDeliveryAddress().toLowerCase() + "%"),
                        cb.like(cb.lower(addressJoin.get("zipcode")),
                                "%" + filter.getDeliveryAddress().toLowerCase() + "%")
                ));
            }

            // Фильтр по временному интервалу
            if (filter.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), filter.getStartDate()));
            }
            if (filter.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), filter.getEndDate()));
            }

            // Фильтр по способу оплаты
            if (filter.getPaymentType() != null) {
                Join<Order, Payment> paymentJoin = root.join("payments");
                predicates.add(cb.equal(
                        paymentJoin.get("paymentType"),
                        filter.getPaymentType()
                ));
            }

            // Фильтр по статусу оплаты
            if (filter.getPaymentStatus() != null) {
                Join<Order, Payment> paymentJoin = root.join("payments");
                predicates.add(cb.equal(
                        paymentJoin.get("status"),
                        filter.getPaymentStatus()
                ));
            }

            // Фильтр по статусу заказа
            if (filter.getOrderStatus() != null) {
                predicates.add(cb.equal(
                        root.get("status"),
                        filter.getOrderStatus()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}