package edu.hotproperties.final_project.repository;

import edu.hotproperties.final_project.entities.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findAllByOrderByPriceDesc();

    Property findByTitle(String title);
}
