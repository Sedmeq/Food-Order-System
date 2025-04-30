package org.example.foodordersystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FoodRequestDto {
    @NotBlank(message = "foodName must not be null or blank")
    private String foodName;

    @NotBlank(message = "categoryName must not be null or blank")
    private String categoryName;

    @NotBlank(message = "description must not be null or blank")
    private String description;
}
