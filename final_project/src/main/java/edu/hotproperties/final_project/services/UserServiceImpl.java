package edu.hotproperties.final_project.services;


import edu.hotproperties.final_project.emuns.Role;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import edu.hotproperties.final_project.entities.Message;

import edu.hotproperties.final_project.entities.Property;
import edu.hotproperties.final_project.entities.User;
import edu.hotproperties.final_project.repository.FavoriteRepository;
import edu.hotproperties.final_project.repository.MessageRepository;
import edu.hotproperties.final_project.repository.PropertyRepository;
import edu.hotproperties.final_project.repository.UserRepository;
import edu.hotproperties.final_project.utils.CurrentUserContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

import java.util.Set;
import java.util.stream.Collectors;



import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FavoriteRepository favoriteRepository;
    private final PropertyRepository propertyRepository;
    private final MessageRepository messageRepository;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           FavoriteRepository favoriteRepository,
                           PropertyRepository propertyRepository,
                           MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.favoriteRepository = favoriteRepository;
        this.propertyRepository = propertyRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }


    @Override
    public User registerNewUser(User user, List<String> roleNames) {
        Set<Role> roles = roleNames.stream()
                .map(roleName ->{
                    try{
                        return Role.valueOf(roleName.toUpperCase());
                    } catch (IllegalArgumentException e){
                        throw new RuntimeException(" Role used is not valid");
                    }
                })
                .collect(Collectors.toSet());

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }



    private CurrentUserContext getCurrentUserContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new CurrentUserContext(user, auth);
    }

    @Override
    public void prepareDashboardModel(Model model) {
        CurrentUserContext context = getCurrentUserContext();
        model.addAttribute("user", context.user());
        model.addAttribute("authorization", context.auth());
    }

    @Override
    public List<Property> getProperties() {
        return propertyRepository.findAllByOrderByPriceDesc();
    }

    @Override
    public Property getProperty(String title) {
        Property property = propertyRepository.findByTitle(title);
        return property;
    }

    @Override
    public List<Property> getFavorites(User user){
        return favoriteRepository.findAllByUser(user);
    }

    @Override
    public List<Property> getManagedProperties(User user) {
        //TODO get all properties where user is agent
        List<Property> managedProperties = propertyRepository.findAllByAgent(user);
        return managedProperties;
    }

    @Override
    public Property addProperty(Property property) {
        //TODO: Validate property
        return propertyRepository.save(property);
    }

    @Override
    public Property updateProperty(Property property) {
        Property updatedProperty = propertyRepository.getById(property.getId());
        //TODO: Error if property not found
        //TODO: Update Property
        return updatedProperty;
    }

    @Override
    public Message messageReply(Message message) {
        Message updatedMessage = messageRepository.getById(message.getId());
        //TODO: Error if property not found
        //TODO: Update Message with reply
        return updatedMessage;
    }
}
