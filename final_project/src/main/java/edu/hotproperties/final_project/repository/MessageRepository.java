package edu.hotproperties.final_project.repository;

import edu.hotproperties.final_project.entities.Favorite;
import edu.hotproperties.final_project.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    //Gets all messages by most recent
    List<Message> findAllByOrderByTimestampDesc();

    //Get one message
    Message getById(Long id);


}
