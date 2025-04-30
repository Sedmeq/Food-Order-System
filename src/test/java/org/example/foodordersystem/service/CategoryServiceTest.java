package org.example.foodordersystem.service;

import org.example.foodordersystem.dto.request.CategoryRequestDto;
import org.example.foodordersystem.dto.response.CategoryDto;
import org.example.foodordersystem.exception.NotFoundException;
import org.example.foodordersystem.model.Category;
import org.example.foodordersystem.model.Menu;
import org.example.foodordersystem.repository.CategoryRepository;
import org.example.foodordersystem.repository.MenuRepository;
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
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDto categoryDto;
    private CategoryRequestDto categoryRequestDto;
    private Menu menu;

    @BeforeEach
    void setUp() {
        // Setup test data
        menu = new Menu();
        menu.setId(1L);
        menu.setMenuName("Test Menu");

        category = new Category();
        category.setId(1L);
        category.setCategoryName("Test Category");
        category.setMenu(menu);

        categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setCategoryName("Test Category");

        categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setCategoryName("Test Category");
        categoryRequestDto.setMenuName("Test Menu");
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() {
        // Arrange
        List<Category> categories = Arrays.asList(category);
        when(categoryRepository.findAll()).thenReturn(categories);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        // Act
        List<CategoryDto> result = categoryService.getAllCategories();

        // Assert
        assertEquals(1, result.size());
        assertEquals(categoryDto.getCategoryName(), result.get(0).getCategoryName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getCategoryById_WithValidId_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        // Act
        CategoryDto result = categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(categoryDto.getCategoryName(), result.getCategoryName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_WithInvalidId_ShouldThrowNotFoundException() {
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(1L));
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryByName_WithValidName_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findByCategoryName(anyString())).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        // Act
        CategoryDto result = categoryService.getCategoryByName("Test Category");

        // Assert
        assertNotNull(result);
        assertEquals(categoryDto.getCategoryName(), result.getCategoryName());
        verify(categoryRepository, times(1)).findByCategoryName("Test Category");
    }

    @Test
    void getCategoryByName_WithInvalidName_ShouldThrowNotFoundException() {
        // Arrange
        when(categoryRepository.findByCategoryName(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> categoryService.getCategoryByName("Invalid Category"));
        verify(categoryRepository, times(1)).findByCategoryName("Invalid Category");
    }

    @Test
    void createCategory_WithValidData_ShouldCreateAndReturnCategory() {
        // Arrange
        when(categoryRepository.existsByCategoryName(anyString())).thenReturn(false);
        when(menuRepository.findByMenuName(anyString())).thenReturn(Optional.of(menu));
        when(modelMapper.map(categoryRequestDto, Category.class)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        // Act
        CategoryDto result = categoryService.createCategory(categoryRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(categoryDto.getCategoryName(), result.getCategoryName());
        verify(categoryRepository, times(1)).existsByCategoryName(categoryRequestDto.getCategoryName());
        verify(menuRepository, times(1)).findByMenuName(categoryRequestDto.getMenuName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void createCategory_WithExistingName_ShouldThrowIllegalArgumentException() {
        // Arrange
        when(categoryRepository.existsByCategoryName(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(categoryRequestDto));
        verify(categoryRepository, times(1)).existsByCategoryName(categoryRequestDto.getCategoryName());
        verify(menuRepository, never()).findByMenuName(anyString());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void createCategory_WithInvalidMenuName_ShouldThrowNotFoundException() {
        // Arrange
        when(categoryRepository.existsByCategoryName(anyString())).thenReturn(false);
        when(menuRepository.findByMenuName(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> categoryService.createCategory(categoryRequestDto));
        verify(categoryRepository, times(1)).existsByCategoryName(categoryRequestDto.getCategoryName());
        verify(menuRepository, times(1)).findByMenuName(categoryRequestDto.getMenuName());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_WithValidData_ShouldUpdateAndReturnCategory() {
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryRepository.existsByCategoryName(anyString())).thenReturn(false);
        doNothing().when(modelMapper).map(categoryRequestDto, category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        // Act
        CategoryDto result = categoryService.updateCategory(1L, categoryRequestDto);

        // Assert
        assertNotNull(result);
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByCategoryName(categoryRequestDto.getCategoryName());
        verify(modelMapper, times(1)).map(categoryRequestDto, category);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void updateCategory_WithExistingName_ShouldThrowIllegalArgumentException() {
        // Arrange
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setCategoryName("Original Category");

        CategoryRequestDto updateRequest = new CategoryRequestDto();
        updateRequest.setCategoryName("Existing Category");

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByCategoryName(updateRequest.getCategoryName())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(1L, updateRequest));
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByCategoryName(updateRequest.getCategoryName());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_WithInvalidId_ShouldThrowNotFoundException() {
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> categoryService.updateCategory(1L, categoryRequestDto));
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).existsByCategoryName(anyString());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategoryById_WithValidId_ShouldDeleteCategory() {
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(any(Category.class));

        // Act
        categoryService.deleteCategoryById(1L);

        // Assert
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategoryById_WithInvalidId_ShouldThrowNotFoundException() {
        // Arrange
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> categoryService.deleteCategoryById(1L));
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}