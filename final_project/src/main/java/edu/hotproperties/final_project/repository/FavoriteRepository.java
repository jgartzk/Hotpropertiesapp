package edu.hotproperties.final_project.repository;

import edu.hotproperties.final_project.entities.Favorite;
import edu.hotproperties.final_project.entities.Property;
import edu.hotproperties.final_project.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findAllByUser(edu.hotproperties.final_project.entities.User user);


    Favorite findByUserAndProperty(edu.hotproperties.final_project.entities.User user, Property property);

    boolean existsByUserAndProperty(User user, Property property);

}
