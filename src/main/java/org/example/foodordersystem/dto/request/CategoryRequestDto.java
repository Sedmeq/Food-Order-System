package org.example.foodordersystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequestDto {
    @NotBlank(message = "categoryName must not be null or blank")
    private String categoryName;

    @NotBlank(message = "menuname must not be null or blank")
    private String menuName;
}
