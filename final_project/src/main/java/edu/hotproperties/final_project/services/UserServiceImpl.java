package edu.hotproperties.final_project.services;

import edu.hotproperties.final_project.entities.Property;
import edu.hotproperties.final_project.entities.User;
import edu.hotproperties.final_project.repository.FavoriteRepository;
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

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FavoriteRepository favoriteRepository;
    private final PropertyRepository propertyRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, FavoriteRepository favoriteRepository, PropertyRepository propertyRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.favoriteRepository = favoriteRepository;
        this.propertyRepository = propertyRepository;
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
        return propertyRepository.findAllByPriceDesc();
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
}
