package it.ispwproject.findyourbook.model;

import it.ispwproject.findyourbook.enumerator.Role;
import it.ispwproject.findyourbook.pattern.observer.Observable;
import java.time.LocalDate;

public abstract class User extends Observable {

    private int id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private String password;
    private LocalDate registrationDate;
    private Role role;

    protected User() {}

    protected User(int id, String name, String surname, String username, String email, String password, LocalDate registrationDate, Role role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.registrationDate = registrationDate;
        this.role = role;
    }


    public void completeRegistration() {
        notifyObservers();
    }


    public String getFullName() {
        return name + " " + surname;
    }

    public boolean hasRole(Role role) {
        return this.role == role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}