package org.example.foodordersystem.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.foodordersystem.dto.request.OrderRequestDto;
import org.example.foodordersystem.dto.response.OrderDto;
import org.example.foodordersystem.enums.OrderStatus;
import org.example.foodordersystem.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Yeni sifariş yaratmaq
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequest) {
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    // Sifarişin statusunu yeniləmək
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    // Bütün sifarişləri gətirmək
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
