package it.ispwproject.findyourbook.dao.memory;

import it.ispwproject.findyourbook.dao.LoginDAO;
import it.ispwproject.findyourbook.demo.DemoDataStore;
import it.ispwproject.findyourbook.exception.LoginException;
import it.ispwproject.findyourbook.model.Credentials;
import it.ispwproject.findyourbook.model.User;

public class LoginDAOMemory implements LoginDAO {

    @Override
    public Credentials execute(String username, String plainPassword) throws LoginException {
        DemoDataStore store = DemoDataStore.getInstance();

        // 1. Cerchiamo l'utente (stesso stile del collega)
        User user = store.getUsers().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(() -> new LoginException("Credenziali non valide. Riprova."));

        if (plainPassword == null || plainPassword.isBlank()) {
            throw new LoginException("Credenziali non valide. Riprova.");
        }

        if (user.getPassword() == null) {
            return new Credentials(username, plainPassword, user.getRole());
        }

        if (!user.getPassword().equals(plainPassword)) {
            throw new LoginException("Credenziali non valide. Riprova.");
        }

        return new Credentials(username, plainPassword, user.getRole());
    }
}