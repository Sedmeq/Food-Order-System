package org.example.foodordersystem.service;

import org.example.foodordersystem.dto.request.FoodRequestDto;
import org.example.foodordersystem.dto.response.FoodDto;
import org.example.foodordersystem.exception.NotFoundException;
import org.example.foodordersystem.model.Category;
import org.example.foodordersystem.model.Food;
import org.example.foodordersystem.repository.CategoryRepository;
import org.example.foodordersystem.repository.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private FoodService foodService;

    private Food food;
    private FoodDto foodDto;
    private FoodRequestDto foodRequestDto;
    private Category category;

    @BeforeEach
    void setUp() {
        // Setup test data
        category = new Category();
        category.setId(1L);
        category.setCategoryName("Test Category");

        food = new Food();
        food.setId(1L);
        food.setFoodName("Test Food");
        food.setDescription("Test Description");
        food.setCategory(category);

        foodDto = new FoodDto();
        foodDto.setId(1L);
        foodDto.setFoodName("Test Food");
        foodDto.setDescription("Test Description");

        foodRequestDto = new FoodRequestDto();
        foodRequestDto.setFoodName("Test Food");
        foodRequestDto.setDescription("Test Description");
        foodRequestDto.setCategoryName("Test Category");
    }

    @Test
    void getAllFoods_ShouldReturnAllFoods() {
        // Arrange
        List<Food> foods = Arrays.asList(food);
        when(foodRepository.findAll()).thenReturn(foods);
        when(modelMapper.map(food, FoodDto.class)).thenReturn(foodDto);

        // Act
        List<FoodDto> result = foodService.getAllFoods();

        // Assert
        assertEquals(1, result.size());
        assertEquals(foodDto.getFoodName(), result.get(0).getFoodName());
        verify(foodRepository, times(1)).findAll();
    }

    @Test
    void getFoodById_WithValidId_ShouldReturnFood() {
        // Arrange
        when(foodRepository.findById(anyLong())).thenReturn(Optional.of(food));
        when(modelMapper.map(food, FoodDto.class)).thenReturn(foodDto);

        // Act
        FoodDto result = foodService.getFoodById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(foodDto.getFoodName(), result.getFoodName());
        assertEquals(foodDto.getDescription(), result.getDescription());
        verify(foodRepository, times(1)).findById(1L);
    }

    @Test
    void getFoodById_WithInvalidId_ShouldThrowNotFoundException() {
        // Arrange
        when(foodRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> foodService.getFoodById(1L));
        verify(foodRepository, times(1)).findById(1L);
    }

    @Test
    void getFoodByName_WithValidName_ShouldReturnFood() {
        // Arrange
        when(foodRepository.findByFoodName(anyString())).thenReturn(Optional.of(food));
        when(modelMapper.map(food, FoodDto.class)).thenReturn(foodDto);

        // Act
        FoodDto result = foodService.getFoodByName("Test Food");

        // Assert
        assertNotNull(result);
        assertEquals(foodDto.getFoodName(), result.getFoodName());
        assertEquals(foodDto.getDescription(), result.getDescription());
        verify(foodRepository, times(1)).findByFoodName("Test Food");
    }

    @Test
    void getFoodByName_WithInvalidName_ShouldThrowNotFoundException() {
        // Arrange
        when(foodRepository.findByFoodName(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> foodService.getFoodByName("Invalid Food"));
        verify(foodRepository, times(1)).findByFoodName("Invalid Food");
    }

    @Test
    void createFood_WithValidData_ShouldCreateAndReturnFood() {
        // Arrange
        when(foodRepository.existsByFoodName(anyString())).thenReturn(false);
        when(categoryRepository.findByCategoryName(anyString())).thenReturn(Optional.of(category));
        when(modelMapper.map(foodRequestDto, Food.class)).thenReturn(food);
        when(foodRepository.save(any(Food.class))).thenReturn(food);
        when(modelMapper.map(food, FoodDto.class)).thenReturn(foodDto);

        // Act
        FoodDto result = foodService.createFood(foodRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(foodDto.getFoodName(), result.getFoodName());
        assertEquals(foodDto.getDescription(), result.getDescription());
        verify(foodRepository, times(1)).existsByFoodName(foodRequestDto.getFoodName());
        verify(categoryRepository, times(1)).findByCategoryName(foodRequestDto.getCategoryName());
        verify(foodRepository, times(1)).save(food);
    }

    @Test
    void createFood_WithExistingName_ShouldThrowIllegalArgumentException() {
        // Arrange
        when(foodRepository.existsByFoodName(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> foodService.createFood(foodRequestDto));
        verify(foodRepository, times(1)).existsByFoodName(foodRequestDto.getFoodName());
        verify(categoryRepository, never()).findByCategoryName(anyString());
        verify(foodRepository, never()).save(any(Food.class));
    }

    @Test
    void createFood_WithInvalidCategoryName_ShouldThrowNotFoundException() {
        // Arrange
        when(foodRepository.existsByFoodName(anyString())).thenReturn(false);
        when(categoryRepository.findByCategoryName(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> foodService.createFood(foodRequestDto));
        verify(foodRepository, times(1)).existsByFoodName(foodRequestDto.getFoodName());
        verify(categoryRepository, times(1)).findByCategoryName(foodRequestDto.getCategoryName());
        verify(foodRepository, never()).save(any(Food.class));
    }

    @Test
    void updateFood_WithValidData_ShouldUpdateAndReturnFood() {
        // Arrange
        when(foodRepository.findById(anyLong())).thenReturn(Optional.of(food));
        when(foodRepository.existsByFoodName(anyString())).thenReturn(false);
        doNothing().when(modelMapper).map(foodRequestDto, food);
        when(foodRepository.save(any(Food.class))).thenReturn(food);
        when(modelMapper.map(food, FoodDto.class)).thenReturn(foodDto);

        // Act
        FoodDto result = foodService.updateFood(1L, foodRequestDto);

        // Assert
        assertNotNull(result);
        verify(foodRepository, times(1)).findById(1L);
        verify(foodRepository, times(1)).existsByFoodName(foodRequestDto.getFoodName());
        verify(modelMapper, times(1)).map(foodRequestDto, food);
        verify(foodRepository, times(1)).save(food);
    }

    @Test
    void updateFood_WithExistingName_ShouldThrowIllegalArgumentException() {
        // Arrange
        Food existingFood = new Food();
        existingFood.setId(1L);
        existingFood.setFoodName("Original Food");

        FoodRequestDto updateRequest = new FoodRequestDto();
        updateRequest.setFoodName("Existing Food");

        when(foodRepository.findById(anyLong())).thenReturn(Optional.of(existingFood));
        when(foodRepository.existsByFoodName(updateRequest.getFoodName())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> foodService.updateFood(1L, updateRequest));
        verify(foodRepository, times(1)).findById(1L);
        verify(foodRepository, times(1)).existsByFoodName(updateRequest.getFoodName());
        verify(foodRepository, never()).save(any(Food.class));
    }

    @Test
    void updateFood_WithInvalidId_ShouldThrowNotFoundException() {
        // Arrange
        when(foodRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> foodService.updateFood(1L, foodRequestDto));
        verify(foodRepository, times(1)).findById(1L);
        verify(foodRepository, never()).existsByFoodName(anyString());
        verify(foodRepository, never()).save(any(Food.class));
    }

    @Test
    void deleteFoodById_WithValidId_ShouldDeleteFood() {
        // Arrange
        when(foodRepository.findById(anyLong())).thenReturn(Optional.of(food));
        doNothing().when(foodRepository).delete(any(Food.class));

        // Act
        foodService.deleteFoodById(1L);

        // Assert
        verify(foodRepository, times(1)).findById(1L);
        verify(foodRepository, times(1)).delete(food);
    }

    @Test
    void deleteFoodById_WithInvalidId_ShouldThrowNotFoundException() {
        // Arrange
        when(foodRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> foodService.deleteFoodById(1L));
        verify(foodRepository, times(1)).findById(1L);
        verify(foodRepository, never()).delete(any(Food.class));
    }
}