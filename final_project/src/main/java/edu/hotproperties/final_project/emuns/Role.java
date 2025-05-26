package edu.hotproperties.final_project.emuns;

public enum Role {
    ADMIN, BUYER, AGENT;


    public String getRole() {

        if(this == ADMIN)
            return "ADMIN";
        else if (this == BUYER) {
            return "BUYER";
        }
        else if (this == AGENT)
            return "AGENT";
        return null;
    }
}