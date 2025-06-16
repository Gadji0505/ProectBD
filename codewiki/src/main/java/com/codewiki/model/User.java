package com.codewiki.model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private int reputation;
    private Timestamp registrationDate;
    private boolean isAdmin;

    // Конструкторы
    public User() {}

    public User(int id, String username, String email, String password, 
               int reputation, Timestamp registrationDate, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.reputation = reputation;
        this.registrationDate = registrationDate;
        this.isAdmin = isAdmin;
    }

    // Геттеры
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getReputation() {
        return reputation;
    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    // Сеттеры
    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", reputation=" + reputation +
               ", registrationDate=" + registrationDate +
               ", isAdmin=" + isAdmin +
               '}';
    }
}