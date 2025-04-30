package org.example.foodordersystem.service;

import lombok.RequiredArgsConstructor;
import org.example.foodordersystem.dto.request.FoodRequestDto;
import org.example.foodordersystem.dto.response.FoodDto;
import org.example.foodordersystem.exception.NotFoundException;
import org.example.foodordersystem.model.Category;
import org.example.foodordersystem.model.Food;
import org.example.foodordersystem.repository.CategoryRepository;
import org.example.foodordersystem.repository.FoodRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    public List<FoodDto> getAllFoods() {
        List<Food> foods = foodRepository.findAll();
        return foods.stream()
                .map(food -> modelMapper.map(food, FoodDto.class))
                .collect(Collectors.toList());
    }

    public FoodDto getFoodById(Long id) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Food not found with id " + id));
        return modelMapper.map(food, FoodDto.class);
    }

    public FoodDto getFoodByName(String foodName) {
        Food food = foodRepository.findByFoodName(foodName)
                .orElseThrow(() -> new NotFoundException("Food not found with name: " + foodName));
        return modelMapper.map(food, FoodDto.class);
    }

    public FoodDto createFood(FoodRequestDto foodRequestDto) {
        if (foodRepository.existsByFoodName(foodRequestDto.getFoodName())) {
            throw new IllegalArgumentException("Food with this name already exists");
        }
        Category category = categoryRepository.findByCategoryName(foodRequestDto.getCategoryName())
                .orElseThrow(() -> new NotFoundException("Category not found"));
        Food food = modelMapper.map(foodRequestDto, Food.class);
        food.setCategory(category);
        return modelMapper.map(foodRepository.save(food), FoodDto.class);
    }

    public FoodDto updateFood(Long id, FoodRequestDto foodRequestDto) {
        Food existingFood = foodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Food not found with id " + id));

        if (foodRepository.existsByFoodName(foodRequestDto.getFoodName()) &&
                !existingFood.getFoodName().equals(foodRequestDto.getFoodName())) {
            throw new IllegalArgumentException("Food with this name already exists");
        }
        modelMapper.map(foodRequestDto, existingFood);
        Food updatedFood = foodRepository.save(existingFood);
        return modelMapper.map(updatedFood, FoodDto.class);
    }

    public void deleteFoodById(Long id) {
        Food existingFood = foodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Food not found with id " + id));
        foodRepository.delete(existingFood);
    }
}
