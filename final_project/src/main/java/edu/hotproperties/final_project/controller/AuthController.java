package edu.hotproperties.final_project.controller;

import edu.hotproperties.final_project.enums.Role;
import edu.hotproperties.final_project.entities.Property;
import edu.hotproperties.final_project.services.AuthService;
import edu.hotproperties.final_project.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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


    @GetMapping({"/", ""})
    public String loginRedirect() {
        return "redirect:/login";
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
                               @RequestParam(value = "file", required = false) MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        try {
            // First, register the user (this will assign them an ID)
            User savedUser = userService.registerNewUser(user, Role.ROLE_BUYER);

            redirectAttributes.addFlashAttribute("successMessage", "Registration successful.");
            return "redirect:/login";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        userService.prepareProfileModel(model);
        return "profile";
    }

    @GetMapping("/profile/edit")
    @PreAuthorize("isAuthenticated()")
    public String editProfile(@RequestParam(required = false) boolean err,
                              Model model){
        userService.prepareEditProfileModel(model, err);
        return "edit_profile";
    }

    @PostMapping("/profile/edit")
    @PreAuthorize("isAuthenticated()")
    public String editProfile(
                              @RequestParam(required = false) String firstName,
                              @RequestParam(required = false) String lastName,
                              @RequestParam(required = false) String email
                              ) {
        try {
            userService.postEditProfile(firstName, lastName, email);
            return "redirect:/profile";
        }
        catch (Exception e){
            return "redirect:/profile/edit?err=true";
        }
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('BUYER', 'AGENT', 'ADMIN')")
    public String dashboard(Model model) {
        userService.prepareDashboardModel(model);
        return "dashboard";
    }

    //BUYER FUNCTIONALITY
    @GetMapping("/buyer/properties/list")
    @PreAuthorize("hasRole('BUYER')")
    public String properties(Model model) {
        model.addAttribute("properties", userService.getProperties());
        return "browse_properties";
    }

    @GetMapping("/buyer/properties/view")
    @PreAuthorize("hasRole('BUYER')")
    public String viewing(@RequestParam(required = true) Long id, Model model) {
        userService.preparePropertyView(id, model);
        return "property_details";
    }

    @GetMapping("/buyer/favorites")
    //@PreAuthorize("hasRole('BUYER')")
    public String favorites(Model model) {
        userService.prepareFavoritesModel(model);
        return "favorites";
    }

    @GetMapping("/buyer/favorites/remove")
    //@PreAuthorize("hasRole('BUYER')")
    public String removeFavorite(@RequestParam (required = true) Long id) {
        userService.removeFavorite(id);
        return "redirect:/buyer/favorites";
    }

    @PostMapping("/buyer/favorites/add")
    //@PreAuthorize("hasRole('BUYER')")
    public String addFavorite(@RequestParam(required = true) Long id) {

        try {
            userService.addFavorite(id);
            return "redirect:/buyer/properties/view?id="+id;
        }
        catch (Exception e) {
            return "redirect:/buyer/properties/view?id="+id;
        }
    }

    @PostMapping("buyer/message")
    //@PreAuthorize("hasRole('BUYER')")
    public String contactAgent(@RequestParam(required = true) Long id,
                               @RequestParam(required = true) String message)
    {
        userService.sendMessage(id, message);
        return "redirect:/buyer/properties/view?id=" + id;
    }

    @GetMapping("/messages")
    //@PreAuthorize("hasRole('BUYER')")
    public String viewBuyerMessages(Model model) {
        userService.prepareMessagesModel(model);
        return "messages";
    }

    @GetMapping("/message")
    //@PreAuthorize("hasRole('BUYER')")
    public String viewBuyerMessages(@RequestParam(required=true) Long id, Model model) {
        userService.prepareViewMessageModel(id, model);
        return "view_message";
    }



    //ROLE_AGENT FUNCTIONALITY

    //Create new property -- get form/screen
    @GetMapping("/agent/new_property")
    public String addPropertyForm(@RequestParam(required = false) boolean err,
                                  Model model) {
        userService.prepareNewPropertyModel(model, err);
        return "new_property";
    }

    //Post new property
    @PostMapping("/agent/new_property")
    public String createProperty(@RequestParam(required = true) String title,
                                 @RequestParam(required = true) String price,
                                 @RequestParam(required = true) String location,
                                 @RequestParam(required = true) String description,
                                 @RequestParam(required = true) String size
                                )
    {
        try {
            double priceDbl = Double.parseDouble(price);
            int sizeInt = Integer.parseInt(size);
            userService.createProperty(new Property(title, priceDbl, location, description, sizeInt));
            return "redirect:/agent/manage_listings";
        }
        catch (Exception e) {
            return "redirect:/agent/new_property?err=true";
        }
    }

    //Get existing property by id and update it

    @PostMapping("/agent/edit_listing")
    //@PreAuthorize("hasRole('AGENT')")
    public String editProperty(@RequestParam(name="id") Long id,
                               @RequestParam(required = true) String title,
                               @RequestParam(required = true) String price,
                               @RequestParam(required = true) String location,
                               @RequestParam(required = true) String description,
                               @RequestParam(required = true) String size
                               ) {
        try {
            double priceDbl = Double.parseDouble(price);
            int sizeInt = Integer.parseInt(size);
            userService.updateProperty(id, title, priceDbl, location, description, sizeInt); //returns updated property
            return "redirect:/agent/manage_listings";
        }
        catch (Exception e){
            return "redirect:/agent/edit_listing?id="+id+"&err=true";
        }
    }

    @GetMapping("/agent/edit_listing")
    //@PreAuthorize("hasRole('AGENT')")
    public String prepareEditPropertyModel(@RequestParam(name="id") Long id,
                                           @RequestParam(required = false) boolean err,
                                           Model model) {

        userService.prepareEditPropertyModel(id, model, err); //returns updated property
        return "edit_listing";
    }

    //Returns a list of properties managed by the agent (current user)
    @GetMapping("/agent/manage_listings")
    public String getManagedProperties(Model model) {
        userService.prepareManagedListingsModel(model);
        return "manage_listings";
    }


    //Agent replies to buyer messages
    @PostMapping("/agent/message/reply")
    @PreAuthorize("hasRole('AGENT')")
    public String viewMessage(@RequestParam(name="id") Long id, @RequestBody String reply) {
        //User adds sender info and message to screen
        userService.postMessageReply(id, reply);
        return "redirect:/message?id=" + id;
    }

    //ADMIN FUNCTIONALITY
    @GetMapping("/admin/users")
    public String viewUsers(Model model) {
        userService.prepareViewUsersModel(model);
        return "user_list";
    }

    @GetMapping("/admin/users/delete")
    public String deleteUser(@RequestParam(required = true) Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/agent/delete_listing")
    public String deleteListing(@RequestParam(required = true) Long id) {
        userService.deleteListing(id);
        return "redirect:/agent/manage_listings";
    }

    @GetMapping("/admin/create_agent")
    public String createAgent(@RequestParam(required = false) boolean err,
                              Model model) {
        model.addAttribute("user", new User());
        if (err) {
            model.addAttribute("errorMessage", "Could not create agent.");
        }
        return "new_agent";
    }

    @PostMapping("/admin/create_agent")
    public String createAgent(@ModelAttribute("user") User user) {
        try {
            userService.registerNewUser(user, Role.ROLE_AGENT);
            return "redirect:/admin/users";
        }
        catch (Exception e) {
            return "redirect:/admin/create_agent?err=true";
        }
    }

}



