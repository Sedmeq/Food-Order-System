package org.example.foodordersystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.foodordersystem.enums.Payment;

import java.util.List;

@Data
public class OrderRequestDto {
    @NotBlank(message = "Address not be empty or null")
    private String address;

    @NotNull(message = "Payment not be empty or null")
    private Payment payment;

    private String cardNumber;

    @Size(min = 1)
    private List<String> foodNames;
}
