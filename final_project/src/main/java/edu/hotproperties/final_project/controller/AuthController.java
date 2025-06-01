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
        List<Property> favorites = userService.getFavorites(current);
        model.addAttribute("properties", favorites);
        return "favorites";
    }

    //AGENT FUNCTIONALITY

    //Create new property
    @PostMapping("/properties/add")
    @PreAuthorize("hasRole('AGENT')")
    public String createProperty(@RequestBody Property property) {
        userService.createProperty(property);
        return "new_property";
    }

    //Get existing property by id and update it
    @PreAuthorize("hasRole('AGENT')")
    public void editProperty(@RequestParam(name="id") Long id, @ModelAttribute("property") Property property) {
        userService.updateProperty(id, property); //returns updated property
    }

    @GetMapping("/properties/edit")
    @PreAuthorize("hasRole('AGENT')")
    public void prepareEditPropertyModel(@RequestParam(name="id") Long id, Model model) {
        userService.prepareEditPropertyModel(id, model); //returns updated property
    }

    //Returns a list of properties managed by the agent (current user)
    @GetMapping("/properties/manage")
    @PreAuthorize("hasRole('AGENT')")
    public String getManagedProperties(Model model) {
        User agent = authService.getCurrentUser();
        userService.prepareManagedListingsModel(agent, model);
        return "manage_listings";
    }

    //Get all messages for Agent
    @GetMapping("/messages")
    @PreAuthorize("hasRole('AGENT')")
    public String getMessages(Model model) {
        //User service adds Agent's list of messages to the model
        userService.prepareMessagesModel(model);
        return "messages";
    }

    //Agent replies to buyer messages
    @GetMapping("/message")
    @PreAuthorize("hasRole('AGENT')")
    public String viewMessage(@RequestParam(name="id") Long id, Model model) {
        //User adds sender info and message to screen
        userService.prepareViewMessageModel(id, model);
        return "view_message";
    }

    //Agent replies to buyer messages
    @PostMapping("/message")
    @PreAuthorize("hasRole('AGENT')")
    public String viewMessage(@RequestParam(name="id") Long id, @RequestBody String reply) {
        //User adds sender info and message to screen
        userService.postMessageReply(id, reply);
        return "view_message";
    }

    //Agent replies to buyer messages
    @PostMapping("/messages/reply")
    @PreAuthorize("hasRole('AGENT')")
    public Message messageReply(@RequestBody Message message, Model model) {
        return userService.messageReply(message);
    }

}
