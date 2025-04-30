package org.example.foodordersystem.service.security;

import io.jsonwebtoken.Claims;
import org.example.foodordersystem.model.User;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;

public interface JwtService {
    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> resolver);

    List<String> getRolesFromToken(String token);

    boolean isValid(String token, UserDetails user);

    String generateToken(User user);
}
