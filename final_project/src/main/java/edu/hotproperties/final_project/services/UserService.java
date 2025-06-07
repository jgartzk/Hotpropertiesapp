package edu.hotproperties.final_project.services;

import edu.hotproperties.final_project.enums.Role;
import edu.hotproperties.final_project.entities.Favorite;
import edu.hotproperties.final_project.entities.Property;
import edu.hotproperties.final_project.entities.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;

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

    User registerNewUser(User user, Role role);

    User getCurrentUser();

    @PreAuthorize("isAuthenticated()")
    void postEditProfile(String firstName, String lastName, String email);

    @PreAuthorize("isAuthenticated()")
    void prepareEditProfileModel(Model model,boolean err);

    //@PreAuthorize("hasRole('ROLE_AGENT')")
    void createProperty(Property property);

    //@PreAuthorize("hasRole('ROLE_AGENT')")
    void updateProperty(Long id, String title, Double price, String location, String description, int size);

    //@PreAuthorize("hasRole('ROLE_AGENT')")
    void prepareEditPropertyModel(Long id, Model model, boolean err);

    //@PreAuthorize("hasRole('ROLE_AGENT')")
    void prepareManagedListingsModel(Model model);

    //@PreAuthorize("hasRole('ROLE_AGENT')")
    void prepareMessagesModel(Model model);

    //@PreAuthorize("hasRole('ROLE_AGENT')")
    void prepareNewPropertyModel(Model model, boolean err);
    //@PreAuthorize("hasRole('ROLE_AGENT')")
    void prepareViewMessageModel(Long id, Model model);

//    @PreAuthorize("hasRole('Agent')")
//    Message messageReply(Message message);

    void preparePropertyView(Long id, Model model);

    void sendMessage(Long id, String message);

    //@PreAuthorize("hasRole('BUYER')")
    void removeFavorite(Long id);

    //@PreAuthorize("hasRole('BUYER')")
    void addFavorite(Long id);

    Property getPropertyById(Long id);

    void postMessageReply(Long id, String reply);

    void prepareViewUsersModel(Model model);
    void deleteUser(Long id);

    void prepareCreateAgentModel(Model model);

    void deleteListing(Long id);

    void prepareFavoritesModel(Model model);

    Long getUserCount();
}

