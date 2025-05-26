package edu.hotproperties.final_project.utils;

import edu.hotproperties.final_project.entities.User;
import org.springframework.security.core.Authentication;

public record CurrentUserContext(User user, Authentication auth) {}
