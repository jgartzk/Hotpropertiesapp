package edu.hotproperties.final_project.emuns;

public enum Role {
    ADMIN, BUYER, AGENT;


    public String getRole() {

        return "ROLE_" + this.name();
    }
}