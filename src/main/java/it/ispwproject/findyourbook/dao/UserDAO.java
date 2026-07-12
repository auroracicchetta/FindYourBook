package it.ispwproject.findyourbook.dao;

import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.User;
import java.util.List;

public interface UserDAO {

    User findByUsername(String username) throws DAOException;

    List<User> getAll() throws DAOException;

    void updateEmail(String username, String newEmail) throws DAOException;
}