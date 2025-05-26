package edu.hotproperties.final_project.services;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;

public interface UserService {
    @PreAuthorize("isAuthenticated()")
    void prepareDashboardModel(Model model);
}
