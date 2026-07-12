package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.dao.DAOFactory;
import it.ispwproject.findyourbook.dao.UserDAO;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.util.ValidationUtils;

public class UserController {

    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = DAOFactory.getUserDAO();
    }

    public void updateEmail(String newEmail) throws DAOException {
        if (newEmail == null || newEmail.isBlank())
            throw new DAOException("L'email non può essere vuota.");
        if (!ValidationUtils.isValidEmail(newEmail))
            throw new DAOException("Email non valida.");

        String username = SessionManager.getInstance().getLoggedUser().getUsername();

        userDAO.updateEmail(username, newEmail);

        SessionManager.getInstance().getLoggedUser().setEmail(newEmail);
    }
}