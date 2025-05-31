package edu.hotproperties.final_project.services;

import edu.hotproperties.final_project.entities.Message;
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

    @PreAuthorize("hasRole('BUYER','Agent')")
    Property getProperty(String title);

    @PreAuthorize("hasRole('Buyer')")
    List<Property> getFavorites(User user);


    User registerNewUser(User user, List<String> roleNames);



    User getCurrentUser();

    @PreAuthorize("hasRole('Agent')")
    List<Property> getManagedProperties(User user);

    @PreAuthorize("hasRole('Agent')")
    Property addProperty(Property property);

    @PreAuthorize("hasRole('Agent')")
    Property updateProperty(Property property);

    @PreAuthorize("hasRole('Agent')")
    Message messageReply(Message message);

}

