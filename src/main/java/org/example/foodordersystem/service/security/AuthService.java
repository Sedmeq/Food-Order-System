package org.example.foodordersystem.service.security;

import org.example.foodordersystem.dto.security.AuthResponse;
import org.example.foodordersystem.dto.security.LoginDTO;
import org.example.foodordersystem.dto.security.RegisterDTO;

public interface AuthService {
    AuthResponse register(RegisterDTO request);
    AuthResponse login(LoginDTO request);
}
