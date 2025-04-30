package org.example.foodordersystem.service;


import lombok.RequiredArgsConstructor;
import org.example.foodordersystem.dto.request.OrderRequestDto;
import org.example.foodordersystem.dto.response.OrderDto;
import org.example.foodordersystem.enums.OrderStatus;
import org.example.foodordersystem.enums.Payment;
import org.example.foodordersystem.exception.NotFoundException;
import org.example.foodordersystem.model.Food;
import org.example.foodordersystem.model.Order;
import org.example.foodordersystem.repository.FoodRepository;
import org.example.foodordersystem.repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final FoodRepository foodRepository;

    @Transactional
    public OrderDto createOrder(OrderRequestDto orderRequest) {
        // Ödəniş kartla olarsa, kart nömrəsini yoxla
        if (orderRequest.getPayment() == Payment.CARD) {
            if (orderRequest.getCardNumber() == null || orderRequest.getCardNumber().length() != 16) {
                throw new IllegalArgumentException("Card Number must consist of exactly 16 characters");
            }
        } else {
            orderRequest.setCardNumber(null); // CASH ödənişində kart məlumatı saxlanmamalıdır
        }

        // Yemək adlarını yoxla
        if (orderRequest.getFoodNames() == null || orderRequest.getFoodNames().isEmpty()) {
            throw new IllegalArgumentException("Food list cannot be empty");
        }

        // Yeməkləri DB-dən götür və `Food` obyektlərinə çevir
        List<Food> foods = orderRequest.getFoodNames().stream()
                .map(name -> foodRepository.findByFoodName(name)
                        .orElseThrow(() -> new NotFoundException("Food not found: " + name)))
                .collect(Collectors.toList());

        // DTO-dan Entity yarat
        Order order = new Order();
        order.setAddress(orderRequest.getAddress());
        order.setPayment(orderRequest.getPayment());
        order.setCardNumber(orderRequest.getCardNumber());
        order.setFoods(foods);
        order.setStatus(OrderStatus.PENDING);

        // Məlumat bazasına əlavə et
        Order savedOrder = orderRepository.save(order);

        // **Cavab DTO yarat və məlumatları set et**
        OrderDto responseDto = modelMapper.map(savedOrder, OrderDto.class);

        // **`foodNames` siyahısını əl ilə set et**
        responseDto.setFoodNames(foods.stream().map(Food::getFoodName).collect(Collectors.toList()));

        return responseDto;
    }


    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);

        // OrderDto-ya map etməzdən əvvəl foodNames-i topla
        List<String> foodNames = order.getFoods() != null
                ? order.getFoods().stream().map(Food::getFoodName).collect(Collectors.toList())
                : Collections.emptyList();

        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
        orderDto.setFoodNames(foodNames);

        return orderDto;
    }


    @Transactional
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> {
                    OrderDto orderDto = modelMapper.map(order, OrderDto.class);
                    orderDto.setFoodNames(order.getFoods().stream().map(Food::getFoodName).collect(Collectors.toList()));
                    return orderDto;
                })
                .collect(Collectors.toList());
    }
}
