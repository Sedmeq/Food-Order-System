package org.example.foodordersystem.dto.response;

import lombok.Data;

@Data
public class FoodDto {
    private Long id;
    private String foodName;
    private String categoryName;
    private String description;
}
