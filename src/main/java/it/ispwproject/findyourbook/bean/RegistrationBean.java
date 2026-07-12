package it.ispwproject.findyourbook.bean;

import it.ispwproject.findyourbook.enumerator.Role;
import java.time.LocalDate;

public class RegistrationBean {

    private String name;
    private String surname;
    private String username;
    private String email;
    private LocalDate birthDate;
    private String password;
    private String confirmPassword;
    private Role role;
    private String description;

    public RegistrationBean() {
        // Costruttore di default vuoto necessario per l'istanziazione del Bean
    }

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

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}