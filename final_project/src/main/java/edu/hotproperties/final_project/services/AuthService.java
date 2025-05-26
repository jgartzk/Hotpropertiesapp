package edu.hotproperties.final_project.services;


import edu.hotproperties.final_project.dtos.JwtResponse;
import edu.hotproperties.final_project.entities.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

public interface AuthService {
    JwtResponse authenticateAndGenerateToken(User user);

    public Cookie loginAndCreateJwtCookie(User user) throws BadCredentialsException;

    void clearJwtCookie(HttpServletResponse response);

}