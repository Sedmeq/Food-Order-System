package org.example.foodordersystem.dto.response;

import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    private String categoryName;
    private String menuName;
}
