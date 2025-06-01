package edu.hotproperties.final_project.controller;

import edu.hotproperties.final_project.entities.Message;
import edu.hotproperties.final_project.entities.Property;
import edu.hotproperties.final_project.services.AuthService;
import edu.hotproperties.final_project.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import edu.hotproperties.final_project.entities.User;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    public AuthController( AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping({"/", "/index"})
    public String showIndex() {
        return "index";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute("user") User user,
                               HttpServletResponse response,
                               Model model) {
        try {
            Cookie jwtCookie = authService.loginAndCreateJwtCookie(user);
            response.addCookie(jwtCookie);
            return "redirect:/dashboard";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }

    @GetMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public String logout(HttpServletResponse response) {
        authService.clearJwtCookie(response);
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                               @RequestParam("selectedRoles") List<String> roleNames,
                               @RequestParam(value = "file", required = false) MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        try {
            // First, register the user (this will assign them an ID)
            User savedUser = userService.registerNewUser(user, roleNames);

            redirectAttributes.addFlashAttribute("successMessage", "Registration successful.");
            return "redirect:/login";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
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

    //Create new property -- get form/screen
    @GetMapping("/properties/add")
    @PreAuthorize("hasRole('AGENT'")
    public String addPropertyForm(Model model) {
        // not needed at the moment: userService.prepareAddPropertyModel(model);
        return "new_property";
    }

    //Post new property
    @PostMapping("/properties/add")

    @PreAuthorize("hasRole('AGENT')")
    public String createProperty(@RequestBody Property property) {
        userService.createProperty(property);
        return "new_property";
    }

    //Get existing property by id and update it

  @PostMapping("/properties/edit")
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
    @PreAuthorize("hasRole('AGENT'")
    public String getMessages(Model model) {
        //User service adds Agent's list of messages to the model
        userService.prepareMessagesModel(model);
        return "messages";
    }

    //Agent replies to buyer messages
    @GetMapping("/message")
    @PreAuthorize("hasRole('AGENT'")
    public String viewMessage(@RequestParam(name="id") Long id, Model model) {
        //User adds sender info and message to screen
        userService.prepareViewMessageModel(id, model);
        return "view_message";
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

    //Agent replies to buyer messages
    @PostMapping("/message")
    @PreAuthorize("hasRole('AGENT'")
    public String viewMessage(@RequestParam(name="id") Long id, @RequestBody String reply) {
        //User adds sender info and message to screen
        userService.postMessageReply(id, reply);
        return "view_message";
    }

}
