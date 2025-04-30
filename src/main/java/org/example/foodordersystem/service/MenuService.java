package org.example.foodordersystem.service;

import lombok.RequiredArgsConstructor;
import org.example.foodordersystem.dto.request.MenuRequestDto;
import org.example.foodordersystem.dto.response.MenuDto;
import org.example.foodordersystem.exception.NotFoundException;
import org.example.foodordersystem.model.Menu;
import org.example.foodordersystem.repository.MenuRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final ModelMapper modelMapper;


    public List<MenuDto> getAllMenus() {
        List<Menu> menus = menuRepository.findAll();
        return menus.stream().map(menu -> modelMapper.map(menu, MenuDto.class))
                .collect(Collectors.toList());
    }

    public MenuDto getMenuById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Menu not found with id " + id));
        return modelMapper.map(menu, MenuDto.class);
    }

    public MenuDto getMenusByName(String menuName) {
        Menu menu = menuRepository.findByMenuName(menuName)
                .orElseThrow(() -> new NotFoundException("No menu found with the name: " + menuName));
        return modelMapper.map(menu, MenuDto.class);
    }

    public MenuDto createMenu(MenuRequestDto menuRequestDto) {
        if (menuRepository.existsByMenuName(menuRequestDto.getMenuName())) {
            throw new IllegalArgumentException("Menu name already exists");
        }
        Menu menu = modelMapper.map(menuRequestDto, Menu.class);
        return modelMapper.map(menuRepository.save(menu), MenuDto.class);
    }

    public MenuDto updateMenu(Long id, MenuRequestDto menuRequestDto) {
        Menu existingMenu = menuRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Menu not found with id " + id));

        if (menuRepository.existsByMenuName(menuRequestDto.getMenuName()) &&
                !existingMenu.getMenuName().equals(menuRequestDto.getMenuName())) {
            throw new IllegalArgumentException("A menu with this name already exists.");
        }
        existingMenu.setMenuName(menuRequestDto.getMenuName());

        Menu updatedMenu = menuRepository.save(existingMenu);
        return modelMapper.map(updatedMenu, MenuDto.class);
    }

    public void deleteMenuById(Long id) {
        Menu exidtingMenu = menuRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Menu not found with id " + id));
        menuRepository.delete(exidtingMenu);
    }
}
