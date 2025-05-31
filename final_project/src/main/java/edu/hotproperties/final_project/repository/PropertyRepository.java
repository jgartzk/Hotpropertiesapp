package edu.hotproperties.final_project.repository;

import edu.hotproperties.final_project.entities.Property;
import edu.hotproperties.final_project.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findAllByOrderByPriceDesc();

    Property findByTitle(String title);
    Property getById(Long id);
    List<Property> findAllByAgent(User user);
}
