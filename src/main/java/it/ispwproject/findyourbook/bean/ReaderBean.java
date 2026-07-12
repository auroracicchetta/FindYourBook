package it.ispwproject.findyourbook.bean;

import java.time.LocalDate;

public class ReaderBean {
    private String username;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthDate;
    private LocalDate registrationDate;

    public ReaderBean() {}

    public ReaderBean(String username, String name, String surname, String email, LocalDate birthDate, LocalDate registrationDate) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthDate = birthDate;
        this.registrationDate = registrationDate;
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

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
}