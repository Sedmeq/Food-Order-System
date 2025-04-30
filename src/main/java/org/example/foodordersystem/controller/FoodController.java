package org.example.foodordersystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.foodordersystem.dto.request.FoodRequestDto;
import org.example.foodordersystem.dto.response.FoodDto;
import org.example.foodordersystem.service.FoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/food")
public class FoodController {

    private final FoodService foodService;

    @GetMapping("/all/foods")
    public ResponseEntity<List<FoodDto>> getAllFoods() {
        return ResponseEntity.ok(foodService.getAllFoods());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodDto> getFoodById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(foodService.getFoodById(id));
    }

    @GetMapping("/search/{foodName}")
    public ResponseEntity<FoodDto> getFoodByName(@PathVariable("foodName") String foodName) {
        return ResponseEntity.ok(foodService.getFoodByName(foodName));
    }

    @PostMapping
    public ResponseEntity<FoodDto> createFood(@RequestBody @Valid FoodRequestDto foodRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(foodService.createFood(foodRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodDto> updateFood(@PathVariable Long id, @RequestBody FoodRequestDto foodRequestDto) {
        return ResponseEntity.ok(foodService.updateFood(id, foodRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodById(@PathVariable("id") Long id) {
        foodService.deleteFoodById(id);
        return ResponseEntity.noContent().build();
    }
}
