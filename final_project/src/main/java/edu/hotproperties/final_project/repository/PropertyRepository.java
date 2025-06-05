package edu.hotproperties.final_project.repository;

import edu.hotproperties.final_project.entities.Property;
import edu.hotproperties.final_project.entities.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.events.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findAllByOrderByPriceDesc();
    Property findByTitle(String title);
    List<Property> findAllByAgent(User user);
    boolean existsByTitle(String title);
}
