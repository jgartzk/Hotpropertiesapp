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

    @PreAuthorize("isAuthenticated()")
    void prepareProfileModel(Model model);

    @PreAuthorize("hasRole('BUYER')")
    List<Property> getProperties();

    @PreAuthorize("hasAnyRole('BUYER','Agent')")
    Property getProperty(String title);

    @PreAuthorize("hasRole('BUYER')")
    List<Property> getFavorites(User user);

    User registerNewUser(User user, List<String> roleNames);

    User getCurrentUser();

    @PreAuthorize("isAuthenticated()")
    void postEditProfile(String firstName, String lastName, String email);

    @PreAuthorize("isAuthenticated()")
    void prepareEditProfileModel(Model model);

    //@PreAuthorize("hasRole('AGENT')")
    void createProperty(Property property);

    //@PreAuthorize("hasRole('AGENT')")
    void updateProperty(Long id, String title, Double price, String location, String description, int size);

    //@PreAuthorize("hasRole('AGENT')")
    void prepareEditPropertyModel(Long id, Model model);

    //@PreAuthorize("hasRole('AGENT')")
    void prepareManagedListingsModel(Model model);

    //@PreAuthorize("hasRole('AGENT')")
    void prepareMessagesModel(Model model);

    //@PreAuthorize("hasRole('AGENT')")
    void prepareNewPropertyModel(Model model);
    //@PreAuthorize("hasRole('AGENT')")
    void prepareViewMessageModel(Long id, Model model);

//    @PreAuthorize("hasRole('Agent')")
//    Message messageReply(Message message);

    @PreAuthorize("hasRole('BUYER')")
    Favorite removeFavorite(User user, Property property);

    @PreAuthorize("hasRole('BUYER')")
    Favorite addFavorite(Favorite favorite);

    boolean isFavorite(User user, Property property);

    Property getPropertyById(Long id);

    void postMessageReply(Long id, String reply);

}

