package edu.hotproperties.final_project.services;

import edu.hotproperties.final_project.entities.Property;
import edu.hotproperties.final_project.entities.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UserService {
    @PreAuthorize("isAuthenticated()")
    void prepareDashboardModel(Model model);

    @PreAuthorize("hasRole('BUYER')")
    List<Property> getProperties();

    @PreAuthorize("hasRole('BUYER')")
    Property getProperty(String title);

    @PreAuthorize("hasRole('Buyer')")
    List<Property> getFavorites(User user);

    User registerNewUser(User user, List<String> roleNames);



    User getCurrentUser();
}
