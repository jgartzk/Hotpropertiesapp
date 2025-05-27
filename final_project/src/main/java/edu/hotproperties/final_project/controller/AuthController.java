package edu.hotproperties.final_project.controller;

import edu.hotproperties.final_project.entities.Property;
import edu.hotproperties.final_project.services.AuthService;
import edu.hotproperties.final_project.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import edu.hotproperties.final_project.entities.User;

import java.util.List;

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

    @GetMapping("/properties/list")
    @PreAuthorize("hasRole('BUYER')")
    public String properties(Model model) {
        model.addAttribute("properties", userService.getProperties());
        return "browse_properties";
    }

    @GetMapping("/properties/view")
    @PreAuthorize("hasRole('BUYER')")
    public String viewing(@RequestParam String title, Model model) {
        Property priority = userService.getProperty(title);
        model.addAttribute("property",priority);
        return "property_details";
    }

    @GetMapping("/favorites/favorites")
    @PreAuthorize("hasRole('Buyer')")
    public String favorites(Model model) {
        User current = authService.getCurrentUser();
        model.addAttribute("properties", current);
        return "favorites";
    }

}
