package com.example.lab7.model.dto.response;

import com.example.lab7.model.dto.request.*;
import com.example.lab7.model.entity.*;
import com.example.lab7.model.value.Address;
import com.example.lab7.repository.CustomerRepository;
import com.example.lab7.repository.ItemRepository;
import com.example.lab7.repository.OrderRepository;
import org.hibernate.Hibernate;

import java.util.stream.Collectors;

public class DtoMapper {

    public static CustomerResponseDto toCustomerDto(Customer customer) {
        CustomerResponseDto dto = new CustomerResponseDto();
        dto.setName(customer.getName());
        if (customer.getAddress() != null) {
            dto.setCity(customer.getAddress().getCity());
            dto.setStreet(customer.getAddress().getStreet());
            dto.setZipcode(customer.getAddress().getZipcode());
        }
        if (Hibernate.isInitialized(customer.getOrders())) {
            dto.setOrders(customer.getOrders().stream()
                    .map(DtoMapper::toOrderDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }


    public static OrderResponseDto toOrderDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setDate(order.getDate());
        dto.setStatus(order.getStatus().name());
        dto.setPayments(order.getPayments().stream()
                .map(DtoMapper::toPaymentDto)
                .collect(Collectors.toList()));
        dto.setOrderDetails(order.getOrderDetails().stream()
                .map(DtoMapper::toOrderDetailDto)
                .collect(Collectors.toList()));
        return dto;
    }

    public static OrderDetailResponseDto toOrderDetailDto(OrderDetail detail) {
        OrderDetailResponseDto dto = new OrderDetailResponseDto();
        dto.setId(detail.getId());
        dto.setItemName(detail.getItem() != null ? detail.getItem().getDescription() : null);
        dto.setQuantity(detail.getQuantity().getValue());
        dto.setQuantityName(detail.getQuantity().getName());
        dto.setQuantitySymbol(detail.getQuantity().getSymbol());
        return dto;
    }

    public static Customer toCustomer(CustomerRequestDto dto) {
        Customer customer = new Customer();
        customer.setName(dto.getName());
        if (dto.getCity() != null || dto.getStreet() != null || dto.getZipcode() != null) {
            customer.setAddress(new Address(dto.getCity(), dto.getStreet(), dto.getZipcode()));
        }
        return customer;
    }

    public static Payment toPayment(PaymentRequestDto dto, OrderRepository orderRepository) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + dto.getOrderId()));

        switch (dto.getPaymentType()) {
            case CASH:
                return new Cash(
                        dto.getAmount(),
                        order,
                        dto.getStatus(),
                        dto.getCashTendered()
                );
            case CHECK:
                return new Check(
                        dto.getAmount(),
                        order,
                        dto.getStatus(),
                        dto.getName(),
                        dto.getBankID()
                );
            case CREDIT:
                return new Credit(
                        dto.getAmount(),
                        order,
                        dto.getStatus(),
                        dto.getNumber(),
                        dto.getType(),
                        dto.getExpDate()
                );
            default:
                throw new IllegalArgumentException("Unknown payment type: " + dto.getPaymentType());
        }
    }

    public static PaymentResponseDto toPaymentDto(Payment payment) {
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus().name());
        dto.setPaymentType(payment.getPaymentType().name());

        // Добавляем специфичные поля для каждого типа платежа
        if (payment instanceof Cash) {
            dto.setCashTendered(((Cash) payment).getCashTendered());
        } else if (payment instanceof Check) {
            dto.setName(((Check) payment).getName());
            dto.setBankID(((Check) payment).getBankID());
        } else if (payment instanceof Credit) {
            dto.setNumber(((Credit) payment).getNumber());
            dto.setType(((Credit) payment).getType());
            dto.setExpDate(((Credit) payment).getExpDate());
        }

        return dto;
    }

    public static Item toItem(ItemRequestDto dto) {
        Item item = new Item();
        item.setShippingWeight(dto.getShippingWeight());
        item.setDescription(dto.getDescription());
        return item;
    }

    public static Order toOrder(OrderRequestDto dto, CustomerRepository customerRepository) {
        Order order = new Order();
        order.setDate(dto.getDate());
        order.setStatus(dto.getStatus());

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + dto.getCustomerId()));

        order.setCustomer(customer);
        return order;
    }

    public static ItemResponseDto toItemDto(Item item) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setDescription(item.getDescription());
        dto.setShippingWeight(item.getShippingWeight());
        return dto;
    }

    public static OrderDetail toOrderDetail(OrderDetailRequestDto dto,
                                            OrderRepository orderRepository,
                                            ItemRepository itemRepository) {
        OrderDetail detail = new OrderDetail();
        detail.setQuantity(dto.getQuantity());
        detail.setTaxStatus(dto.getTaxStatus());
        detail.setOrder(orderRepository.findById(dto.getOrderId()).orElse(null));
        detail.setItem(itemRepository.findById(dto.getItemId()).orElse(null));
        return detail;
    }
}
