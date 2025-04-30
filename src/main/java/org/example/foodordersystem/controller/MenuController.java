package org.example.foodordersystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.foodordersystem.dto.request.MenuRequestDto;
import org.example.foodordersystem.dto.response.MenuDto;
import org.example.foodordersystem.service.MenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/all")
    public ResponseEntity<List<MenuDto>> GetAllMenus() {
        return ResponseEntity.ok(menuService.getAllMenus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuDto> GetMenuById(@PathVariable Long id) {
        return ResponseEntity.ok(menuService.getMenuById(id));
    }

    @GetMapping("/search/{menuName}")
    public ResponseEntity<MenuDto> getMenuByName(@PathVariable String menuName) {
        return ResponseEntity.ok(menuService.getMenusByName(menuName));
    }

    @PostMapping
    public ResponseEntity<MenuDto> createMenu(@RequestBody @Valid MenuRequestDto menuRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createMenu(menuRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuDto> updateMenu(@PathVariable Long id, @RequestBody MenuRequestDto menuRequestDto) {
        return ResponseEntity.ok(menuService.updateMenu(id, menuRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuById(@PathVariable Long id) {
        menuService.deleteMenuById(id);
        return ResponseEntity.noContent().build();
    }
}
