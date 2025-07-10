package com.bib.app.entities;

import lombok.Data;
import jakarta.persistence.Id;


import jakarta.persistence.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Admin {
    @Id
    private String username;
    
    private String id = UUID.randomUUID().toString();
    private String password;
    private String role;
    // Lombok @Data should handle getters/setters, but keeping these for compatibility
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
}