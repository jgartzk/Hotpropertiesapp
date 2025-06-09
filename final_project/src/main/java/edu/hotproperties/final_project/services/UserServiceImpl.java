package edu.hotproperties.final_project.services;

import edu.hotproperties.final_project.entities.Favorite;

import edu.hotproperties.final_project.enums.Role;

import edu.hotproperties.final_project.entities.Message;

import edu.hotproperties.final_project.entities.Property;
import edu.hotproperties.final_project.entities.User;
import edu.hotproperties.final_project.exceptions.*;
import edu.hotproperties.final_project.repository.FavoriteRepository;
import edu.hotproperties.final_project.repository.MessageRepository;
import edu.hotproperties.final_project.repository.PropertyRepository;
import edu.hotproperties.final_project.repository.UserRepository;
import edu.hotproperties.final_project.utils.CurrentUserContext;
import jakarta.el.PropertyNotFoundException;
import jakarta.servlet.http.Cookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;


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
    public User registerNewUser(User user, Role role) {

        user.addRole(role);
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
    public void postEditProfile(String firstName, String lastName, String email)  {


        //Get current user
        User currentUser = getCurrentUserContext().user();

        //Set posted fields
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setEmail(email);

        validUser(currentUser); //Throw error if invalid user

        //Save update user details
        userRepository.save(currentUser);
    }

    @Override
    public void prepareEditProfileModel(Model model, boolean err) {
        CurrentUserContext context = getCurrentUserContext();
        model.addAttribute("firstName", context.user().getFirstName());
        model.addAttribute("lastName", context.user().getLastName());
        model.addAttribute("email", context.user().getEmail());
        model.addAttribute("user", context.user());
        if (err) {
            model.addAttribute("errorMessage", "Could not update profile");
        }
    }

    @Override
    public void prepareDashboardModel(Model model) {
        CurrentUserContext context = getCurrentUserContext();
        model.addAttribute("user", context.user());
        model.addAttribute("authorization", context.auth());
    }


    @Override
    public void addFavorite(Long id) {
        Property property = propertyRepository.findById(id)
                        .orElseThrow(() -> new PropertyNotFoundException("Property with id {"+id+"} not found"));

        //create new favorite with property, user, time created
        Favorite favorite = new Favorite(getCurrentUserContext().user(), property, LocalDateTime.now());

        //validate favorite and save
        validFavorite(favorite);
        favoriteRepository.save(favorite);
    }

    @Transactional
    @Override
    public void removeFavorite(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException("Property with id {"+id+"} not found"));
        Favorite favorite = favoriteRepository.findByUserAndProperty(getCurrentUserContext().user(), property);
        favoriteRepository.delete(favorite);
    }

    @Transactional
    @Override
    public void deleteListing(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException("Property with id {"+id+"} not found"));
        propertyRepository.delete(property);
    }



    @Override
    public void prepareFavoritesModel(Model model) {
        //Get all favorited properties by user
        User user = getCurrentUserContext().user();
        List<Favorite> favorites = favoriteRepository.findAllByUser(user);

        //add favorite properties to model
        model.addAttribute("favorites", favorites);

    }

    @Override
    public void createProperty(Property property) {

        property.setAgent(getCurrentUserContext().user()); //Set agent to creator of agent
        //Validate property, int must be positive and string must not be empty
        validProperty(property);
        propertyRepository.save(property);
    }


    @Override
    public void updateProperty(Long id, String title, Double price, String description, String location, int size) {
        //Get existing property from db
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException("Property with id {"+id+"} not found"));

        //update with changes
        property.setTitle(title);
        property.setPrice(price);
        property.setDescription(description);
        property.setLocation(location);
        property.setSize(size);
        property.setSize(size);

        //save changes
        propertyRepository.save(property);
    }
    @Override
    public void preparePropertyView(Long id, Model model) {

        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException("Property with id {"+id+"} not found"));

        //check if message sent
        if (messageRepository.existsBySenderAndProperty(getCurrentUserContext().user(), property)) {
            model.addAttribute("messageSent",true);
        }
        else {
            model.addAttribute("messageSent",false);
            model.addAttribute("message", new Message());
        }

        //check if favorited
        User user = getCurrentUserContext().user();
        if (favoriteRepository.existsByUserAndProperty(user, property)) {
            model.addAttribute("favorited",true);
        }

        model.addAttribute("property",property);
        model.addAttribute("id", id);

        User buyer = getCurrentUserContext().user();
    }

    @Override
    public void sendMessage(Long id, Message message) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException("Property with id {"+id+"} not found"));

        User sender = getCurrentUserContext().user();
        message.setProperty(property);
        message.setSender(sender);
        message.setTimestamp(LocalDateTime.now());
        validMessage(message);
        messageRepository.save(message);

        property.addMessage(message);
        propertyRepository.save(property);
    }

   @Override
    public void prepareBrowsePropertiesModel(Model model) {
        model.addAttribute("properties", propertyRepository.findAll());
   }
    @Override
    public void prepareManagedListingsModel(Model model) {
        User agent = getCurrentUserContext().user();
        List<Property> properties = propertyRepository.findAllByAgent(agent);

        model.addAttribute("role", agent.getRoles());
        model.addAttribute("properties", properties);
    }
    @Override
    public void prepareNewPropertyModel(Model model, boolean err){
        if (err) {
            model.addAttribute("errorMessage", "Could not create new property.");
        }
        model.addAttribute("property", new Property());
    }

    @Override
    public void prepareViewMessageModel(Long id, Model model) {
        //Get message by id and return it to model
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException("Property with id {"+id+"} not found"));

        if (getCurrentUserContext().user().getRoles().contains(Role.AGENT)) {
            model.addAttribute("isAgent", true);
        }
        model.addAttribute("message", message);
        model.addAttribute("replyMessage", new Message());

    }



    @Override
    public void prepareEditPropertyModel(Long id, Model model, boolean err){
            Property property = propertyRepository.findById(id)
                    .orElseThrow(() -> new PropertyNotFoundException("Property with id {"+id+"} not found"));
                model.addAttribute("propertyId", id);
                model.addAttribute("propertyTitle", property.getTitle());
                model.addAttribute("propertyPrice", property.getPrice());
                model.addAttribute("propertyLocation", property.getLocation());
                model.addAttribute("propertyDescription", property.getDescription());
                model.addAttribute("propertySize", property.getSize());

                if (err) {
                    model.addAttribute("errorMessage", "Could not update property");
                }
    }

    @Override
    public void postMessageReply(Long id, Message message) {
        //make sure reply is not blank
        if (!message.getReply().isEmpty()) {
            //Save message with reply to db
            Message oldMessage = messageRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Message not found"));
            oldMessage.setReply(message.getReply());
            messageRepository.save(oldMessage);
        }
    }

    @Transactional
    @Override
    public void deleteMessage(Long id){

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Message Not Found"));
        messageRepository.delete(message);
    }

  public void prepareMessagesModel(Model model) {
        List<Message> messages = new ArrayList<Message>();

        //Get current user (Agent)
        User user = getCurrentUserContext().user();

        if (user.getRoles().contains(Role.BUYER)) {
            messages = messageRepository.findAllBySender(user);
        }
        else if (user.getRoles().contains(Role.AGENT)) {
              //For all properties managed by agent
              List<Property> properties = propertyRepository.findAllByAgent(user);
              for (Property property : properties) {
                  //If message exists at property, add to the model
                  if (messageRepository.existsByProperty(property)) {
                      messages.addAll(messageRepository.findAllByProperty(property));
                  }
              }
        }

        //Add all messages to model
        model.addAttribute("messages", messages);
    }

    public void prepareViewUsersModel(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void prepareCreateAgentModel(Model model){
        model.addAttribute("user", new User());
    }

    public void postNewAgent(User agent) {
        agent.addRole(Role.AGENT);

        validUser(agent);//throw error if not valid
        userRepository.save(agent);
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
        String[] splitDouble = Double.toString(price).split("\\.");
        if (splitDouble.length > 1) {
            if (splitDouble[1].length() > 2) {
                throw new InvalidPropertyParameterException("Price must have 2 decimal places or less");
            }
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
    }

    public Long getUserCount() {
        return userRepository.count();

    }

}
