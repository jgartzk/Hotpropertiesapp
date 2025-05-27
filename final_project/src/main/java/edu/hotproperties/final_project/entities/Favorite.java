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

    public Favorite() {}

    public Favorite(User user, Property property) {
        this.user = user;
        this.property = property;
    }

    public Favorite(User user, Property property, LocalDateTime createdAt) {
        this.user = user;
        this.property = property;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
