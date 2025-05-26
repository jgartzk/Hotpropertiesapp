package edu.hotproperties.final_project.controller;

import edu.hotproperties.final_project.services.AuthService;
import edu.hotproperties.final_project.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
    public String dashboard(Model model) {
        userService.prepareDashboardModel(model);
        return "dashboard";
    }

}
