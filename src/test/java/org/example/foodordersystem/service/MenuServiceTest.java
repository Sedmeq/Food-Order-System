package org.example.foodordersystem.service;

import org.example.foodordersystem.dto.request.MenuRequestDto;
import org.example.foodordersystem.dto.response.MenuDto;
import org.example.foodordersystem.exception.NotFoundException;
import org.example.foodordersystem.model.Menu;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MenuService menuService;

    private Menu menu;
    private MenuDto menuDto;
    private MenuRequestDto menuRequestDto;

    @BeforeEach
    void setUp() {
        // Setup test data
        menu = new Menu();
        menu.setId(1L);
        menu.setMenuName("Test Menu");

        menuDto = new MenuDto();
        menuDto.setId(1L);
        menuDto.setMenuName("Test Menu");

        menuRequestDto = new MenuRequestDto();
        menuRequestDto.setMenuName("Test Menu");
    }

    @Test
    void getAllMenus_ShouldReturnAllMenus() {
        // Arrange
        List<Menu> menus = Arrays.asList(menu);
        when(menuRepository.findAll()).thenReturn(menus);
        when(modelMapper.map(menu, MenuDto.class)).thenReturn(menuDto);

        // Act
        List<MenuDto> result = menuService.getAllMenus();

        // Assert
        assertEquals(1, result.size());
        assertEquals(menuDto.getMenuName(), result.get(0).getMenuName());
        verify(menuRepository, times(1)).findAll();
    }

    @Test
    void getMenuById_WithValidId_ShouldReturnMenu() {
        // Arrange
        when(menuRepository.findById(anyLong())).thenReturn(Optional.of(menu));
        when(modelMapper.map(menu, MenuDto.class)).thenReturn(menuDto);

        // Act
        MenuDto result = menuService.getMenuById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(menuDto.getMenuName(), result.getMenuName());
        verify(menuRepository, times(1)).findById(1L);
    }

    @Test
    void getMenuById_WithInvalidId_ShouldThrowNotFoundException() {
        // Arrange
        when(menuRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> menuService.getMenuById(1L));
        verify(menuRepository, times(1)).findById(1L);
    }

    @Test
    void getMenusByName_WithValidName_ShouldReturnMenu() {
        // Arrange
        when(menuRepository.findByMenuName(anyString())).thenReturn(Optional.of(menu));
        when(modelMapper.map(menu, MenuDto.class)).thenReturn(menuDto);

        // Act
        MenuDto result = menuService.getMenusByName("Test Menu");

        // Assert
        assertNotNull(result);
        assertEquals(menuDto.getMenuName(), result.getMenuName());
        verify(menuRepository, times(1)).findByMenuName("Test Menu");
    }

    @Test
    void getMenusByName_WithInvalidName_ShouldThrowNotFoundException() {
        // Arrange
        when(menuRepository.findByMenuName(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> menuService.getMenusByName("Invalid Menu"));
        verify(menuRepository, times(1)).findByMenuName("Invalid Menu");
    }

    @Test
    void createMenu_WithValidData_ShouldCreateAndReturnMenu() {
        // Arrange
        when(menuRepository.existsByMenuName(anyString())).thenReturn(false);
        when(modelMapper.map(menuRequestDto, Menu.class)).thenReturn(menu);
        when(menuRepository.save(any(Menu.class))).thenReturn(menu);
        when(modelMapper.map(menu, MenuDto.class)).thenReturn(menuDto);

        // Act
        MenuDto result = menuService.createMenu(menuRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(menuDto.getMenuName(), result.getMenuName());
        verify(menuRepository, times(1)).existsByMenuName(menuRequestDto.getMenuName());
        verify(menuRepository, times(1)).save(menu);
    }

    @Test
    void createMenu_WithExistingName_ShouldThrowIllegalArgumentException() {
        // Arrange
        when(menuRepository.existsByMenuName(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> menuService.createMenu(menuRequestDto));
        verify(menuRepository, times(1)).existsByMenuName(menuRequestDto.getMenuName());
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    void updateMenu_WithValidData_ShouldUpdateAndReturnMenu() {
        // Arrange
        when(menuRepository.findById(anyLong())).thenReturn(Optional.of(menu));
        when(menuRepository.existsByMenuName(anyString())).thenReturn(false);
        when(menuRepository.save(any(Menu.class))).thenReturn(menu);
        when(modelMapper.map(menu, MenuDto.class)).thenReturn(menuDto);

        MenuRequestDto updateRequest = new MenuRequestDto();
        updateRequest.setMenuName("Updated Menu");

        // Act
        MenuDto result = menuService.updateMenu(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(menuRepository, times(1)).findById(1L);
        verify(menuRepository, times(1)).existsByMenuName(updateRequest.getMenuName());
        verify(menuRepository, times(1)).save(menu);
    }

    @Test
    void updateMenu_WithExistingName_ShouldThrowIllegalArgumentException() {
        // Arrange
        Menu existingMenu = new Menu();
        existingMenu.setId(1L);
        existingMenu.setMenuName("Original Menu");

        MenuRequestDto updateRequest = new MenuRequestDto();
        updateRequest.setMenuName("Existing Menu");

        when(menuRepository.findById(anyLong())).thenReturn(Optional.of(existingMenu));
        when(menuRepository.existsByMenuName(updateRequest.getMenuName())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> menuService.updateMenu(1L, updateRequest));
        verify(menuRepository, times(1)).findById(1L);
        verify(menuRepository, times(1)).existsByMenuName(updateRequest.getMenuName());
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    void updateMenu_WithInvalidId_ShouldThrowNotFoundException() {
        // Arrange
        when(menuRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> menuService.updateMenu(1L, menuRequestDto));
        verify(menuRepository, times(1)).findById(1L);
        verify(menuRepository, never()).existsByMenuName(anyString());
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    void deleteMenuById_WithValidId_ShouldDeleteMenu() {
        // Arrange
        when(menuRepository.findById(anyLong())).thenReturn(Optional.of(menu));
        doNothing().when(menuRepository).delete(any(Menu.class));

        // Act
        menuService.deleteMenuById(1L);

        // Assert
        verify(menuRepository, times(1)).findById(1L);
        verify(menuRepository, times(1)).delete(menu);
    }

    @Test
    void deleteMenuById_WithInvalidId_ShouldThrowNotFoundException() {
        // Arrange
        when(menuRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> menuService.deleteMenuById(1L));
        verify(menuRepository, times(1)).findById(1L);
        verify(menuRepository, never()).delete(any(Menu.class));
    }
}