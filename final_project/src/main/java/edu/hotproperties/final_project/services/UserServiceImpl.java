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

import java.util.ArrayList;
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

    //Maybe get by property id instead?
    @Override
    public Property getProperty(String title) {
        Property property = propertyRepository.findByTitle(title);
        //TODO: if not found throw PropertyNotFound Exception
        return property;
    }

    @Override
    public List<Property> getFavorites(User user){
        return favoriteRepository.findAllByUser(user);
    }

    @Override
    public void createProperty(Property property) {
        //Validate property, int must be positive and string must not be empty
        if (property.getPrice() > 0 &&
            property.getSize() > 0 &&
            !property.getTitle().isEmpty() &&
            !property.getLocation().isEmpty() &&
            !property.getDescription().isEmpty()
        ) {
            propertyRepository.save(property);
        }
        //TODO: else throw InvalidPropertyException
    }

    @Override
    public void updateProperty(Long id, Property updatedProperty) {
        //Get existing property from db
        Property property = propertyRepository.getById(id);
        //TODO: if property==null throw PropertyNotFound

        //update with changes
        property.setTitle(updatedProperty.getTitle());
        property.setPrice(updatedProperty.getPrice());
        property.setDescription(updatedProperty.getDescription());
        property.setLocation(updatedProperty.getLocation());
        property.setSize(updatedProperty.getSize());

        //save changes
        propertyRepository.save(property);
    }

    @Override
    public void prepareManagedListingsModel(User agent, Model model) {
        List<Property> properties = propertyRepository.findAllByAgent(agent);
        model.addAttribute("properties", properties);
    }

    @Override
    public void prepareViewMessageModel(Long id, Model model) {
        //Get message by id and return it to model
        Message message = messageRepository.getById(id);
        model.addAttribute("message", message);
    }

    @Override
    public void prepareEditPropertyModel(Long id, Model model){
        Property property = propertyRepository.getById(id);
        model.addAttribute("property", property);
    }

    @Override
    public void postMessageReply(Long id, String reply) {
        //Get message w/o reply
        Message message = messageRepository.getById(id);
        //Set reply
        message.setReply(reply);
        //Save message with reply to db
        messageRepository.save(message);
    }

    //Gets all messages and adds them to model
    @Override
    public void prepareMessagesModel(Model model) {
        List<Message> messages = new ArrayList<Message>();

        //Get current user (Agent)
        User currentUser = getCurrentUserContext().user();

        //For all properties managed by agent
        List<Property> properties = propertyRepository.findAllByAgent(currentUser);
        for (Property property : properties) {
            //If message exists at property, add to the model
            if (messageRepository.existsByProperty(property)) {
               messages.add(messageRepository.getByProperty(property));

            }
        }
        //Add all messages to model
        model.addAttribute("messages", messages);
    }
}
