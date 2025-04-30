package org.example.foodordersystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.example.foodordersystem.dto.request.OrderRequestDto;
import org.example.foodordersystem.dto.response.OrderDto;
import org.example.foodordersystem.enums.OrderStatus;
import org.example.foodordersystem.enums.Payment;
import org.example.foodordersystem.exception.NotFoundException;
import org.example.foodordersystem.model.Food;
import org.example.foodordersystem.model.Order;
import org.example.foodordersystem.repository.FoodRepository;
import org.example.foodordersystem.repository.OrderRepository;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ModelMapper modelMapper;


    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderDto orderDto;
    private Food pizza;
    private Food burger;

    @BeforeEach
    public void setUp() {
        // Initialize a sample Order object
        order = new Order();
        order.setId(1L);
        order.setAddress("Test Address");
        order.setPayment(Payment.CASH);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        orderDto = new OrderDto();
        orderDto.setAddress("Test Address");
        orderDto.setPayment(Payment.CASH);
        orderDto.setStatus(OrderStatus.PENDING);

        // Initialize food items
        pizza = new Food();
        pizza.setId(1L);
        pizza.setFoodName("Pizza");

        burger = new Food();
        burger.setId(2L);
        burger.setFoodName("Burger");
    }

    @Test
    void testCreateOrder_withCashPayment_success() {
        // Given
        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setAddress("Test Address");
        requestDto.setPayment(Payment.CASH);
        requestDto.setFoodNames(List.of("Pizza", "Burger"));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setAddress("Test Address");
        savedOrder.setPayment(Payment.CASH);
        savedOrder.setStatus(OrderStatus.PENDING);
        savedOrder.setFoods(List.of(pizza, burger));

        OrderDto responseDto = new OrderDto();
        responseDto.setAddress("Test Address");
        responseDto.setPayment(Payment.CASH);
        responseDto.setStatus(OrderStatus.PENDING);
        responseDto.setFoodNames(List.of("Pizza", "Burger"));

        // When
        when(foodRepository.findByFoodName("Pizza")).thenReturn(Optional.of(pizza));
        when(foodRepository.findByFoodName("Burger")).thenReturn(Optional.of(burger));
        when(orderRepository.save(Mockito.any(Order.class))).thenReturn(savedOrder);
        when(modelMapper.map(savedOrder, OrderDto.class)).thenReturn(responseDto);

        OrderDto response = orderService.createOrder(requestDto);

        // Then
        assertNotNull(response);
        assertEquals("Test Address", response.getAddress());
        assertEquals(Payment.CASH, response.getPayment());
        assertNull(response.getCardNumber());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(List.of("Pizza", "Burger"), response.getFoodNames());

        verify(foodRepository, times(1)).findByFoodName("Pizza");
        verify(foodRepository, times(1)).findByFoodName("Burger");
        verify(orderRepository, times(1)).save(Mockito.any(Order.class));
    }

    @Test
    void testCreateOrder_withCardPayment_success() {
        // Given
        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setAddress("Test Address");
        requestDto.setPayment(Payment.CARD);
        requestDto.setCardNumber("1234567890123456");
        requestDto.setFoodNames(List.of("Pizza", "Burger"));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setAddress("Test Address");
        savedOrder.setPayment(Payment.CARD);
        savedOrder.setCardNumber("1234567890123456");
        savedOrder.setStatus(OrderStatus.PENDING);
        savedOrder.setFoods(List.of(pizza, burger));

        OrderDto responseDto = new OrderDto();
        responseDto.setAddress("Test Address");
        responseDto.setPayment(Payment.CARD);
        responseDto.setCardNumber("1234567890123456");
        responseDto.setStatus(OrderStatus.PENDING);
        responseDto.setFoodNames(List.of("Pizza", "Burger"));

        // When
        when(foodRepository.findByFoodName("Pizza")).thenReturn(Optional.of(pizza));
        when(foodRepository.findByFoodName("Burger")).thenReturn(Optional.of(burger));
        when(orderRepository.save(Mockito.any(Order.class))).thenReturn(savedOrder);
        when(modelMapper.map(savedOrder, OrderDto.class)).thenReturn(responseDto);

        OrderDto response = orderService.createOrder(requestDto);

        // Then
        assertNotNull(response);
        assertEquals("Test Address", response.getAddress());
        assertEquals(Payment.CARD, response.getPayment());
        assertEquals("1234567890123456", response.getCardNumber());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(List.of("Pizza", "Burger"), response.getFoodNames());

        verify(foodRepository, times(1)).findByFoodName("Pizza");
        verify(foodRepository, times(1)).findByFoodName("Burger");
        verify(orderRepository, times(1)).save(Mockito.any(Order.class));
    }

    @Test
    void testCreateOrder_withInvalidCardNumber_shouldThrowException() {
        // Given
        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setPayment(Payment.CARD);
        requestDto.setCardNumber("12345"); // Invalid - too short

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(requestDto)
        );

        assertEquals("Card Number must consist of exactly 16 characters", exception.getMessage());
        verify(orderRepository, never()).save(Mockito.any(Order.class));
    }

    @Test
    void testCreateOrder_withEmptyFoodList_shouldThrowException() {
        // Given
        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setPayment(Payment.CASH);
        requestDto.setFoodNames(Collections.emptyList());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(requestDto)
        );

        assertEquals("Food list cannot be empty", exception.getMessage());
        verify(orderRepository, never()).save(Mockito.any(Order.class));
    }

    @Test
    void testCreateOrder_withUnknownFood_shouldThrowNotFoundException() {
        // Given
        OrderRequestDto requestDto = new OrderRequestDto();
        requestDto.setPayment(Payment.CASH);
        requestDto.setFoodNames(List.of("UnknownFood"));

        // When
        when(foodRepository.findByFoodName("UnknownFood")).thenReturn(Optional.empty());

        // Then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> orderService.createOrder(requestDto)
        );

        assertEquals("Food not found: UnknownFood", exception.getMessage());
        verify(orderRepository, never()).save(Mockito.any(Order.class));
    }


    @Test
    public void testUpdateOrderStatus_Success() {
        // Given
        Long orderId = 1L;
        OrderStatus newStatus = OrderStatus.CONFIRMED;

        // Mock edilmiş yeməklər
        Food pizza = new Food();
        pizza.setId(1L);
        pizza.setFoodName("Pizza");

        Food burger = new Food();
        burger.setId(2L);
        burger.setFoodName("Burger");

        // Mock edilmiş sifariş
        order.setFoods(List.of(pizza, burger)); // <-- bu çox vacibdir!
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // Mapped DTO
        OrderDto mappedDto = new OrderDto();
        mappedDto.setStatus(newStatus);
        mappedDto.setAddress("Some Address"); // əgər varsa

        when(modelMapper.map(order, OrderDto.class)).thenReturn(mappedDto);

        // When
        OrderDto result = orderService.updateOrderStatus(orderId, newStatus);

        // Then
        assertNotNull(result);
        assertEquals(newStatus, result.getStatus());
        assertEquals(List.of("Pizza", "Burger"), result.getFoodNames());

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
        verify(modelMapper, times(1)).map(order, OrderDto.class);
    }



    @Test
    void testUpdateOrderStatus_NotFound() {
        // Given
        Long orderId = 999L;
        OrderStatus newStatus = OrderStatus.DELIVERED;

        // When
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Then
        assertThrows(NotFoundException.class, () -> orderService.updateOrderStatus(orderId, newStatus));
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(Mockito.any(Order.class));
    }

    @Test
    void testGetAllOrders() {
        // Given
        Order order1 = new Order();
        order1.setId(1L);
        order1.setAddress("Address 1");
        order1.setStatus(OrderStatus.PENDING);
        order1.setFoods(List.of(pizza));

        Order order2 = new Order();
        order2.setId(2L);
        order2.setAddress("Address 2");
        order2.setStatus(OrderStatus.DELIVERED);
        order2.setFoods(List.of(burger));

        List<Order> orders = Arrays.asList(order1, order2);

        OrderDto orderDto1 = new OrderDto();

        orderDto1.setAddress("Address 1");
        orderDto1.setStatus(OrderStatus.PENDING);
        orderDto1.setFoodNames(List.of("Pizza"));

        OrderDto orderDto2 = new OrderDto();

        orderDto2.setAddress("Address 2");
        orderDto2.setStatus(OrderStatus.DELIVERED);
        orderDto2.setFoodNames(List.of("Burger"));

        // When
        when(orderRepository.findAll()).thenReturn(orders);
        when(modelMapper.map(order1, OrderDto.class)).thenReturn(orderDto1);
        when(modelMapper.map(order2, OrderDto.class)).thenReturn(orderDto2);

        List<OrderDto> result = orderService.getAllOrders();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Address 1", result.get(0).getAddress());
        assertEquals(OrderStatus.PENDING, result.get(0).getStatus());
        assertEquals(List.of("Pizza"), result.get(0).getFoodNames());

        assertEquals("Address 2", result.get(1).getAddress());
        assertEquals(OrderStatus.DELIVERED, result.get(1).getStatus());
        assertEquals(List.of("Burger"), result.get(1).getFoodNames());

        verify(orderRepository, times(1)).findAll();
    }
}