package edu.hotproperties.final_project.initializer;

import edu.hotproperties.final_project.enums.Role;
import edu.hotproperties.final_project.entities.User;
import edu.hotproperties.final_project.services.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer {

    private final UserService userService;

    public AdminInitializer(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {

        try {
            User admin = new User();
            admin.setFirstName("ADMIN");
            admin.setLastName("USER");
            admin.setEmail("admin@testing.com");
            admin.setPassword("miniproject");

            userService.registerNewUser(admin, Role.ADMIN);
        }
        catch (Exception e){
            //User already in the db
        }
    }
}
