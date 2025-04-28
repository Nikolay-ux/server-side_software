package com.example.lab7.IT;

import com.example.lab7.model.dto.OrderFilterDto;
import com.example.lab7.model.entity.*;
import com.example.lab7.model.value.Address;
import com.example.lab7.model.value.Quantity;
import com.example.lab7.model.value.Weight;
import com.example.lab7.repository.CustomerRepository;
import com.example.lab7.repository.ItemRepository;
import com.example.lab7.repository.OrderRepository;
import com.example.lab7.repository.PaymentRepository;
import com.example.lab7.service.OrderFilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class OrderFilterServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderFilterService filterService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private Customer customer1;
    private Customer customer2;
    private Item item1;
    private Item item2;
    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
        itemRepository.deleteAll();
        customerRepository.deleteAll();

        customer1 = customerRepository.save(new Customer(
                "Иван Иванов",
                new Address("Москва", "ул. Пушкина 10", "101000"))
        );

        customer2 = customerRepository.save(new Customer(
                "Анна Сидорова",
                new Address("Санкт-Петербург", "Невский пр. 25", "190000"))
        );

        item1 = itemRepository.save(new Item(
                new Weight(new BigDecimal("1.5"), "weight", "kg"),
                "Ноутбук")
        );

        item2 = itemRepository.save(new Item(
                new Weight(new BigDecimal("0.3"), "weight", "kg"),
                "Мышь")
        );

        order1 = createOrder(
                LocalDateTime.now().minusDays(2),
                OrderStatus.PROCESSING,
                customer1,
                item1,
                1,
                "STANDARD",
                100.50f,
                PaymentStatus.COMPLETED,
                PaymentType.CREDIT
        );

        order2 = createOrder(
                LocalDateTime.now().minusDays(1),
                OrderStatus.DELIVERED,
                customer2,
                item2,
                2,
                "TAX_FREE",
                25.75f,
                PaymentStatus.PENDING,
                PaymentType.CASH
        );
    }

    private Order createOrder(LocalDateTime date,
                              OrderStatus status,
                              Customer customer,
                              Item item,
                              int quantity,
                              String taxStatus,
                              float amount,
                              PaymentStatus paymentStatus,
                              PaymentType paymentType) {
        Order order = new Order();
        order.setDate(date);
        order.setStatus(status);
        order.setCustomer(customer);

        OrderDetail detail = new OrderDetail(
                new Quantity(quantity, "quantity", "pcs"),
                taxStatus,
                order
        );
        detail.setItem(item);
        order.addOrderDetail(detail);

        Payment payment;
        switch (paymentType) {
            case CASH:
                payment = new Cash(amount, order, paymentStatus, amount + 10);
                break;
            case CREDIT:
                payment = new Credit(amount, order, paymentStatus,
                        "4111111111111111", "VISA", LocalDateTime.now().plusYears(2));
                break;
            case CHECK:
                payment = new Check(amount, order, paymentStatus,
                        "Check 123", "BANK123");
                break;
            default:
                throw new IllegalArgumentException("Unknown payment type");
        }
        order.addPayment(payment);

        return orderRepository.save(order);
    }

    @Test
    void shouldFilterByCustomerName() {
        OrderFilterDto filter = new OrderFilterDto();
        filter.setCustomerName("Иван");

        List<Order> result = filterService.findOrdersByFilter(filter);

        assertEquals(1, result.size());
        assertEquals("Иван Иванов", result.getFirst().getCustomer().getName());
    }

    @Test
    void shouldFilterByDeliveryAddress() {
        OrderFilterDto filter = new OrderFilterDto();
        filter.setDeliveryAddress("ул. Пушкина 10");

        List<Order> result = filterService.findOrdersByFilter(filter);

        assertEquals(1, result.size());
        assertEquals("ул. Пушкина 10", result.getFirst().getCustomer().getAddress().getStreet());
    }

    @Test
    void shouldFilterByDateRange() {
        OrderFilterDto filter = new OrderFilterDto();
        filter.setStartDate(LocalDateTime.now().minusDays(3));
        filter.setEndDate(LocalDateTime.now().minusDays(1).plusHours(1));

        List<Order> result = filterService.findOrdersByFilter(filter);

        assertEquals(2, result.size());
    }

    @Test
    void shouldFilterByOrderStatus() {
        OrderFilterDto filter = new OrderFilterDto();
        filter.setOrderStatus(OrderStatus.PROCESSING);

        List<Order> result = filterService.findOrdersByFilter(filter);

        assertEquals(1, result.size());
        assertEquals(OrderStatus.PROCESSING, result.getFirst().getStatus());
    }

    @Test
    void shouldFilterByPaymentStatus() {
        OrderFilterDto filter = new OrderFilterDto();
        filter.setPaymentStatus(PaymentStatus.COMPLETED);

        List<Order> result = filterService.findOrdersByFilter(filter);

        assertEquals(1, result.size());
        assertTrue(result.getFirst().getPayments().stream()
                .anyMatch(p -> p.getStatus() == PaymentStatus.COMPLETED));
    }

    @Test
    void shouldFilterByPaymentType() {
        OrderFilterDto filter = new OrderFilterDto();
        filter.setPaymentType("CREDIT");

        List<Order> result = filterService.findOrdersByFilter(filter);

        assertEquals(1, result.size());
        assertTrue(result.getFirst().getPayments().stream()
                .anyMatch(p -> p.getPaymentType() == PaymentType.CREDIT));
    }

    @Test
    void shouldReturnEmptyListForNonMatchingFilter() {
        OrderFilterDto filter = new OrderFilterDto();
        filter.setCustomerName("Несуществующее имя");

        List<Order> result = filterService.findOrdersByFilter(filter);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCombineMultipleFilters() {
        OrderFilterDto filter = new OrderFilterDto();
        filter.setStartDate(LocalDateTime.now().minusDays(3));
        filter.setOrderStatus(OrderStatus.PROCESSING);
        filter.setPaymentStatus(PaymentStatus.COMPLETED);

        List<Order> result = filterService.findOrdersByFilter(filter);

        assertEquals(1, result.size());
        assertEquals(order1.getId(), result.getFirst().getId());
    }
}