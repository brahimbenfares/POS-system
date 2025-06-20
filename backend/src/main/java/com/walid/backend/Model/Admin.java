package com.walid.backend.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Admin {
    private int id;
    private String username;
    @JsonProperty("password")  // This tells Jackson to map the 'password' JSON field to this Java field
    private String passwordHash;
    private String roleName;
    private String email;
    private boolean isActive; // Reflecting the is_active field from your database

    public Admin() {
        // Default constructor
    }

    public Admin(int id, String username, String passwordHash, String roleName, String email, boolean isActive) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.roleName = roleName;
        this.email = email;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
