package edu.hotproperties.final_project.controller;

import edu.hotproperties.final_project.entities.Message;
import edu.hotproperties.final_project.entities.Property;
import edu.hotproperties.final_project.services.AuthService;
import edu.hotproperties.final_project.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    //BUYER FUNCTIONALITY
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
    @PreAuthorize("hasRole('BUYER')")
    public String favorites(Model model) {
        User current = authService.getCurrentUser();
        model.addAttribute("properties", current);
        return "favorites";
    }

    //AGENT FUNCTIONALITY

    //Create new property
    @PostMapping("/properties/add")
    @PreAuthorize("hasRole('AGENT'")
    public Property addProperty(@RequestBody Property property, Model model) {
        //TODO: get data from request and create property object
        return userService.addProperty(property); //Return recently created property
    }

    //Get existing property by id and update it
    @PostMapping("/properties/edit")
    @PreAuthorize("hasRole('AGENT'")
    public Property editProperty(@ModelAttribute("property") Property property) {
        return userService.updateProperty(property); //returns updated property
    }

    //Returns a list of properties managed by the agent (current user)
    @GetMapping("/properties/manage")
    @PreAuthorize("hasRole('AGENT'")
    public List<Property> getManagedProperties() {
        return userService.getManagedProperties(authService.getCurrentUser());
    }

    //Agent replies to buyer messages
    @PostMapping("/messages/reply")
    @PreAuthorize("hasRole('AGENT'")
    public Message messageReply(@RequestBody Message message, Model model) {
        return userService.messageReply(message);
    }

}
