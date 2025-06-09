package edu.hotproperties.final_project.services;

import edu.hotproperties.final_project.entities.Message;
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


    @PreAuthorize("hasRole('ADMIN')")
    User registerNewUser(User user, Role role);

    User getCurrentUser();

    @PreAuthorize("isAuthenticated()")
    void postEditProfile(String firstName, String lastName, String email);

    @PreAuthorize("isAuthenticated()")
    void prepareEditProfileModel(Model model,boolean err);

    @PreAuthorize("hasRole('AGENT')")
    void createProperty(Property property);

    @PreAuthorize("hasRole('AGENT')")
    void updateProperty(Long id, String title, Double price, String location, String description, int size);

    @PreAuthorize("hasRole('AGENT')")
    void prepareEditPropertyModel(Long id, Model model, boolean err);

    @PreAuthorize("hasRole('AGENT')")
    void prepareManagedListingsModel(Model model);

    @PreAuthorize("hasAnyRole('AGENT','BUYER')")
    void prepareMessagesModel(Model model);

    @PreAuthorize("hasRole('AGENT')")
    void prepareNewPropertyModel(Model model, boolean err);

    @PreAuthorize("hasAnyRole('AGENT','BUYER')")
    void prepareViewMessageModel(Long id, Model model);

    @PreAuthorize("hasRole('BUYER')")
    void preparePropertyView(Long id, Model model);

    @PreAuthorize("hasRole('BUYER')")
    void sendMessage(Long id, Message message);

    @PreAuthorize("hasRole('BUYER')")
    void removeFavorite(Long id);
    @PreAuthorize("hasRole('BUYER')")
    void addFavorite(Long id);

    @PreAuthorize("hasRole('AGENT')")
    void postMessageReply(Long id, Message message);

    @PreAuthorize("hasRole('ADMIN')")
    void prepareViewUsersModel(Model model);
    @PreAuthorize("hasRole('ADMIN')")
    void deleteUser(Long id);

    @PreAuthorize("hasRole('AGENT')")
    void deleteListing(Long id);

    @PreAuthorize("hasRole('BUYER')")
    void prepareFavoritesModel(Model model);

    Long getUserCount();

    @PreAuthorize("hasRole('BUYER')")
    void prepareBrowsePropertiesModel(Model model);


    @PreAuthorize("hasAnyRole('BUYER','AGENT')")
    void deleteMessage(Long id);
}

