package it.ispwproject.findyourbook.bean;

import java.time.LocalDate;

public class PublisherBean {
    private String username;
    private String name;
    private String surname;
    private String email;
    private LocalDate registrationDate;
    private String description;

    public PublisherBean() {}

    public PublisherBean(String username, String name, String surname, String email, LocalDate registrationDate, String description) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.registrationDate = registrationDate;
        this.description = description;
    }

    public String getFullName() { return name + " " + surname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}