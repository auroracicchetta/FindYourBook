package it.ispwproject.findyourbook.model;

import it.ispwproject.findyourbook.enumerator.Role;
import java.time.LocalDate;

public class Publisher extends User {

    private String description; // Campo esclusivo della Casa Editrice

    public Publisher() {
        super();
    }

    public Publisher(int id, String name, String surname, String username, String email, String password, LocalDate registrationDate, String description) {
        super(id, name, surname, username, email, password, registrationDate, Role.PUBLISHER);
        this.description = description;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}