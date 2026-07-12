package it.ispwproject.findyourbook.dao.memory;

import it.ispwproject.findyourbook.dao.UserDAO;
import it.ispwproject.findyourbook.demo.DemoDataStore;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.User;
import java.util.List;

public class UserDAOMemory implements UserDAO {

    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public User findByUsername(String username) throws DAOException {
        return store.getUsers().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(() -> new DAOException("Utente non trovato: " + username));
    }

    public void updatePassword(int id, String newPassword) throws DAOException {
        store.getUsers().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new DAOException("Utente non trovato (ID: " + id + ")"))
                .setPassword(newPassword);
    }

    @Override
    public List<User> getAll() throws DAOException {
        return List.copyOf(store.getUsers());
    }

    @Override
    public void updateEmail(String username, String newEmail) throws DAOException {
        User user = store.getUsers().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(() -> new DAOException("Utente non trovato: " + username));
        user.setEmail(newEmail);
    }
}