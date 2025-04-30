package org.example.foodordersystem.service;

import lombok.RequiredArgsConstructor;
import org.example.foodordersystem.dto.request.CategoryRequestDto;
import org.example.foodordersystem.dto.response.CategoryDto;
import org.example.foodordersystem.exception.NotFoundException;
import org.example.foodordersystem.model.Category;
import org.example.foodordersystem.model.Menu;
import org.example.foodordersystem.repository.CategoryRepository;
import org.example.foodordersystem.repository.MenuRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final MenuRepository menuRepository;


    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id " + id));
        return modelMapper.map(category, CategoryDto.class);
    }

    public CategoryDto getCategoryByName(String categoryName) {
        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new NotFoundException("No category found with the name: " + categoryName));
        return modelMapper.map(category, CategoryDto.class);
    }

    public CategoryDto createCategory(CategoryRequestDto categoryRequestDto) {
        if (categoryRepository.existsByCategoryName(categoryRequestDto.getCategoryName())) {
            throw new IllegalArgumentException("Category name already exists");
        }
        Menu menu = menuRepository.findByMenuName(categoryRequestDto.getMenuName())
                .orElseThrow(() -> new NotFoundException("Menu not found"));
        Category category = modelMapper.map(categoryRequestDto, Category.class);
        category.setMenu(menu);
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    public CategoryDto updateCategory(Long id, CategoryRequestDto categoryRequestDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id " + id));

        if (categoryRepository.existsByCategoryName(categoryRequestDto.getCategoryName()) &&
                !existingCategory.getCategoryName().equals(categoryRequestDto.getCategoryName())) {
            throw new IllegalArgumentException("A category with this name already exists.");
        }
        modelMapper.map(categoryRequestDto, existingCategory);

        Category updatedCategory = categoryRepository.save(existingCategory);
        return modelMapper.map(updatedCategory, CategoryDto.class);
    }

    public void deleteCategoryById(Long id) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id " + id));
        categoryRepository.delete(existingCategory);
    }
}
