package edu.hotproperties.final_project.services;

import edu.hotproperties.final_project.entities.Favorite;

import edu.hotproperties.final_project.emuns.Role;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import edu.hotproperties.final_project.entities.Message;

import edu.hotproperties.final_project.entities.Property;
import edu.hotproperties.final_project.entities.User;
import edu.hotproperties.final_project.exceptions.*;
import edu.hotproperties.final_project.repository.FavoriteRepository;
import edu.hotproperties.final_project.emuns.Role;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
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

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

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
                .map(roleName -> {
                    try {

                        String role = roleName.toUpperCase().replaceFirst("^ROLE_", "");
                        return Role.valueOf(role);
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Role used is not valid: " + roleName);
                    }
                })
                .collect(Collectors.toSet());

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
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
    public void prepareProfileModel(Model model) {
        CurrentUserContext context = getCurrentUserContext();
        model.addAttribute("firstName", context.user().getFirstName());
        model.addAttribute("lastName", context.user().getLastName());
        model.addAttribute("email", context.user().getEmail());
        model.addAttribute("role", context.user().getRoles());
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
    public Favorite removeFavorite(Favorite favorite) {
        Favorite temp = favorite;
        if(favoriteExists(favorite.getUser(), favorite.getProperty()))
            throw new NotFoundException("Favorite doesn't exist: " + favorite.getProperty());
        favoriteRepository.delete(favorite);
        return temp;
    }

    @Override
    public Favorite addFavorite(Favorite favorite) {
        validFavorite(favorite);
        favoriteRepository.save(favorite);
        return favorite;
    }

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

    //////////////////////////////////////////////////////
    /// Validation Methods
    ///////////////////////////////////////////////////////

    public void validFavorite(Favorite favorite) {
        if (favorite.getUser() == null) {
            throw new InvalidFavoriteParameterException("User is null");
        }
        else if(!userRepository.existsByEmail(favorite.getUser().getEmail())) {
            throw new InvalidFavoriteParameterException("User not found");
        }

        validUser(favorite.getUser());

        if (favorite.getProperty() == null) {
            throw new InvalidFavoriteParameterException("Property is null");
        }
        else if(!propertyRepository.existsByTitle(favorite.getProperty().getTitle())) {
            throw new InvalidFavoriteParameterException("Property not found");
        }

        validProperty(favorite.getProperty());

    }

    public void validUser(User user) {
        validName(user.getFirstName(), user.getLastName());
        validEmail(user.getEmail());
        validPassword(user.getPassword());
    }

    public void validProperty(Property property) {
        validTitle(property.getTitle());
        validPrice(property.getPrice());
        validDescription(property.getDescription());
        validLocation(property.getLocation());
        validSize(property.getSize());
    }

    public void validMessage(Message message) {
        validContent(message.getContent());
        validProperty(message.getProperty());
        userExists(message.getSender().getEmail());
    }

    public void userExists(String email) {
        if(!userRepository.existsByEmail(email)){
            throw new UsernameNotFoundException("User not found");
        }
    }

    public boolean favoriteExists(User user, Property property) {
        if(favoriteRepository.findByUserAndProperty(user, property) != null)
            return true;
        return false;
    }


    public void validContent(String content){
        if(content == null){
            throw new InvalidMessageParameterException("Content is null");
        }
        else if(content.trim().equals("")){
            throw new InvalidMessageParameterException("Content is empty");
        }
    }

    public void validTitle(String title) {
        if (title == null) {
            throw new InvalidPropertyParameterException("Title is null");
        }
        else if(title.trim().equals("")){
            throw new InvalidPropertyParameterException("Title is empty");
        }
    }

    public void validPrice(double price) {
        if (price <= 0) {
            throw new InvalidPropertyParameterException("Price must be greater than 0");
        }
    }

    public void validDescription(String description) {
        if(description == null) {
            throw new InvalidPropertyParameterException("Description is null");
        }
        else if(description.trim().equals("")) {
            throw new InvalidPropertyParameterException("Description is empty");
        }
    }

    public void validLocation(String location) {
        if(location == null) {
            throw new InvalidPropertyParameterException("Location is null");
        }
        else if(location.trim().equals("")) {
            throw new InvalidPropertyParameterException("Location is empty");
        }
    }

    public void validSize(Integer size) {
        if (size == null) {
            throw new InvalidPropertyParameterException("Size is null");
        }
        else if (size <= 0) {
            throw new InvalidPropertyParameterException("Size must be greater than 0");
        }
    }

    public void validName(String firstName, String lastName) {
        if(firstName == null || lastName == null) {
            throw new InvalidUserParameterException("Name is null");
        }
        else if(firstName.trim().equals("") || lastName.trim().equals("")) {
            throw new InvalidUserParameterException("Name is empty");
        }
    }

    public void validEmail(String email) {
        if(email == null) {
            throw new InvalidUserParameterException("Email is null");
        }
        else if(email.trim().equals("")) {
            throw new InvalidUserParameterException("Email is empty");
        }

        if(!email.contains("@")) {
            throw new InvalidUserParameterException("Email is not a valid email");
        }
        else if(!(email.charAt(email.length() - 4) == '.')) {
            throw new InvalidUserParameterException("Email is not a valid email");
        }
    }

    public void validPassword(String password) {
        if(password == null) {
            throw new InvalidUserParameterException("Password is null");
        }
        else if(password.trim().equals("")) {
            throw new InvalidUserParameterException("Password is empty");
        }
   
}}
