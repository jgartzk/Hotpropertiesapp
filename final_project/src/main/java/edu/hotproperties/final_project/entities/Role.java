package edu.hotproperties.final_project.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public enum Role {
    ADMIN,
    AGENT,
    BUYER
}
