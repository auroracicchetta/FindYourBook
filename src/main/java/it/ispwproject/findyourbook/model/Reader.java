package it.ispwproject.findyourbook.model;

import it.ispwproject.findyourbook.enumerator.Role;
import java.time.LocalDate;

public class Reader extends User {

    public Reader() {
        super();
    }

    public Reader(int id, String name, String surname, String username, String password, LocalDate dataNascita) {
        super(id, name, surname, username, password, dataNascita, Role.LETTORE);
    }
}