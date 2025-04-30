package org.example.foodordersystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

@Data
public class MenuRequestDto {
    @NotBlank(message = "menuName must not be null or blank")
    private String menuName;
}
