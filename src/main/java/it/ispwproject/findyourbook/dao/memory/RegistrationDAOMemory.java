package it.ispwproject.findyourbook.dao.memory;

import it.ispwproject.findyourbook.dao.RegistrationDAO;
import it.ispwproject.findyourbook.demo.DemoDataStore;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.User;

public class RegistrationDAOMemory implements RegistrationDAO {
    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public boolean usernameExists(String username) throws DAOException {
        return store.getUsers().stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    @Override
    public void save(User user) throws DAOException {
        user.setId(store.nextUserId());
        store.getUsers().add(user);
    }
}