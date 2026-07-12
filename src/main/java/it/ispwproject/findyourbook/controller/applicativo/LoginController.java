package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.bean.SessionBean;
import it.ispwproject.findyourbook.dao.DAOFactory;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.exception.LoginException;
import it.ispwproject.findyourbook.model.Credentials;
import it.ispwproject.findyourbook.model.User;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.util.PasswordUtils; // <-- AGGIUNTO IMPORT
import it.ispwproject.findyourbook.util.logger.AppLogger;

public class LoginController {

    public enum LoginResult {
        SUCCESSO_READER,
        SUCCESSO_PUBLISHER,
        SUCCESSO_ADMIN
    }

    public LoginResult login(String username, String password) throws LoginException, DAOException {

        String hashedPassword = PasswordUtils.hash(password);

        Credentials credentials = DAOFactory.getLoginDAO().execute(username, hashedPassword);

        User user = null;
        try {
            user = DAOFactory.getUserDAO().findByUsername(username);

        } catch (DAOException e) {
            AppLogger.logError("[LoginController] Errore DAO nel recupero utente: " + e.getMessage());
            throw new LoginException("Errore nel recupero dei dati utente. Riprova più tardi.");
        }

        if (user == null) {
            throw new LoginException("Utente non trovato nel sistema.");
        }

        SessionManager.getInstance().setLoggedUser(user);
        SessionManager.getInstance().setSessionBean(
                new SessionBean(user.getUsername(), credentials.getRole())
        );


        User verificato = SessionManager.getInstance().getLoggedUser();
        if (verificato == null) {
            AppLogger.logError("[LoginController] ERRORE CRITICO: Sessione non impostata!");
            throw new LoginException("Errore interno durante il login. Riprova.");
        }

        return switch (credentials.getRole()) {
            case READER -> LoginResult.SUCCESSO_READER;
            case PUBLISHER -> LoginResult.SUCCESSO_PUBLISHER;
            case ADMIN -> LoginResult.SUCCESSO_ADMIN;
            default -> throw new IllegalStateException("Ruolo non riconosciuto: " + credentials.getRole());
        };
    }
}