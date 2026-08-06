package it.ispwproject.findyourbook.dao;

import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.User;

public interface RegistrationDAO {
    boolean usernameExists(String username) throws DAOException;
    boolean emailExists(String email) throws DAOException;
    void save(User user) throws DAOException;
}