package it.ispwproject.findyourbook.dao.memory;

import it.ispwproject.findyourbook.demo.DemoDataStore;
import it.ispwproject.findyourbook.dao.LoginDAO;
import it.ispwproject.findyourbook.exception.LoginException;
import it.ispwproject.findyourbook.model.Credentials;
import it.ispwproject.findyourbook.model.User;
import it.ispwproject.findyourbook.util.PasswordUtils;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public class LoginDAOMemory implements LoginDAO {

    @Override
    public Credentials execute(String username, String plainPassword) throws LoginException {
        DemoDataStore store = DemoDataStore.getInstance();

        User user = store.getUsers().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(() -> new LoginException("Username non trovato in memoria."));

        // CORREZIONE: Hashiamo la password inserita prima di confrontarla
        String hashedInput = PasswordUtils.hash(plainPassword);

        if (plainPassword == null || !hashedInput.equals(user.getPassword())) {
            AppLogger.logError("Errore: Password non corrispondente!");
            throw new LoginException("Password errata.");
        }

        return new Credentials(username, plainPassword, user.getRole());
    }
}