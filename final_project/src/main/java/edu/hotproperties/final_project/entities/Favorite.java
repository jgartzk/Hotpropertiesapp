package edu.hotproperties.final_project.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "favorties")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    private Property property;

    @Column(nullable = false)
    private LocalDateTime createdAt;


}
