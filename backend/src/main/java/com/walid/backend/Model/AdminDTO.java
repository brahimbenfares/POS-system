package com.walid.backend.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminDTO {
    private int id;
    private String username;
    private String password;
    private String email;
    private String roleName;

    @JsonProperty("isActive")
    private boolean isActive;
    // Default constructor
    public AdminDTO() {
    }

    // Parameterized constructor
    public AdminDTO(String username, String password, String email, String roleName, boolean isActive) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roleName = roleName;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    // Getters and Setters
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

    public String getEmail() { 
        return email;
    }

    public void setEmail(String email) { 
        this.email = email;
    }

    public String getRoleName() { 
        return roleName;
    }

    public void setRoleName(String roleName) { 
        this.roleName = roleName;
    }

    @JsonProperty("isActive")
    public boolean isActive() { 
        return isActive;
    }

    @JsonProperty("isActive")
    public void setActive(boolean isActive) { 
        this.isActive = isActive;
    }
}
