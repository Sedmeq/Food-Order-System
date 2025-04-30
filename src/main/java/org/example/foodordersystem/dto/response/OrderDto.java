package org.example.foodordersystem.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.example.foodordersystem.enums.OrderStatus;
import org.example.foodordersystem.enums.Payment;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private String address;
    private Payment payment;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cardNumber;

    private OrderStatus status;
    private List<String> foodNames;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
