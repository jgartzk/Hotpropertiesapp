package edu.hotproperties.final_project.repository;

import edu.hotproperties.final_project.entities.Favorite;
import edu.hotproperties.final_project.entities.Message;
import edu.hotproperties.final_project.entities.Property;
import edu.hotproperties.final_project.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    //Gets all messages by most recent
    List<Message> findAllByOrderByTimestampDesc();

    //Get one message
    Message getById(Long id);

    Message getByProperty(Property property);

    boolean existsByAgent(User user);

    boolean existsByProperty(Property property);


    List<Message> findAllBySender(User user);

    List<Message> findAllByProperty(Property property);

    boolean existsBySenderAndProperty(User user, Property property);
}
