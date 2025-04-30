package org.example.foodordersystem.repository;

import org.example.foodordersystem.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    boolean existsByMenuName(String menuName);

    Optional<Menu> findByMenuName(String menuName);
}
