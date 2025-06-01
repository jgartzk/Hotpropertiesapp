package edu.hotproperties.final_project.services;

import edu.hotproperties.final_project.entities.Favorite;
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

    @PreAuthorize("hasAnyRole('BUYER','Agent')")
    Property getProperty(String title);

    @PreAuthorize("hasRole('BUYER')")
    List<Property> getFavorites(User user);

    User registerNewUser(User user, List<String> roleNames);

    User getCurrentUser();

    @PreAuthorize("hasRole('Agent')")
    void createProperty(Property property);

    @PreAuthorize("hasRole('Agent')")
    void updateProperty(Long id, Property property);

    @PreAuthorize("hasRole('Agent')")
    void prepareEditPropertyModel(Long id, Model model);

    @PreAuthorize("hasRole('Agent')")
    List<Property> getManagedProperties(User user);

    @PreAuthorize("hasRole('Agent')")
    Property addProperty(Property property);

    @PreAuthorize("hasRole('Agent')")
    Property updateProperty(Property property);

    @PreAuthorize("hasRole('Agent')")
    Message messageReply(Message message);

    @PreAuthorize("hasRole('BUYER')")
    Favorite removeFavorite(Favorite favorite);

    @PreAuthorize("hasRole('BUYER')")
    Favorite addFavorite(Favorite favorite);

    void prepareManagedListingsModel(User agent, Model model);

    void prepareViewMessageModel(Long id, Model model);
}

