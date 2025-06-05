package edu.hotproperties.final_project.repository;

import edu.hotproperties.final_project.entities.Favorite;
import edu.hotproperties.final_project.entities.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Property> findAllByUser(edu.hotproperties.final_project.entities.User user);


    Property findByUserAndProperty(edu.hotproperties.final_project.entities.User user, Property property);

    Favorite findByPropertyAndUser(edu.hotproperties.final_project.entities.User user, Property property);
}
