package it.ispwproject.findyourbook.model;

import it.ispwproject.findyourbook.enumerator.Role;
import java.time.LocalDate;

public class Reader extends User {

    private LocalDate birthDate; // Campo esclusivo del Lettore

    public Reader() {
        super();
    }

    public Reader(int id, String name, String surname, String username, String email, String password, LocalDate registrationDate, LocalDate birthDate) {
        super(id, name, surname, username, email, password, registrationDate, Role.READER);
        this.birthDate = birthDate;
    }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}