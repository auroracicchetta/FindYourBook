package it.ispwproject.findyourbook.bean;

import it.ispwproject.findyourbook.enumerator.Role;
import java.time.LocalDate;

public class RegistrationBean {

    private String name;
    private String surname;
    private String username;
    private String email; // <--- AGGIUNTO
    private LocalDate dataNascita;
    private String password;
    private String confirmPassword;
    private Role role;
    private String descrizione;

    public RegistrationBean() {
        //Costruttore vuoto di default
    }

    // Metodi Get e Set
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; } // <--- AGGIUNTO
    public void setEmail(String email) { this.email = email; } // <--- AGGIUNTO

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public LocalDate getDataNascita() { return dataNascita; }
    public void setDataNascita(LocalDate dataNascita) { this.dataNascita = dataNascita; }
}