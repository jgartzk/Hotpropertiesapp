package edu.hotproperties.final_project.enums;

public enum Role {
    ROLE_ADMIN, ROLE_BUYER, ROLE_AGENT;


    public String getRole() {

        return this.name();
    }
}