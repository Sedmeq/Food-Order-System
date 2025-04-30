package org.example.foodordersystem.repository;

import org.example.foodordersystem.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    boolean existsByFoodName(String foodName);

    Optional<Food> findByFoodName(String foodName);
}