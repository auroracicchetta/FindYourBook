package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.bean.SessionBean;
import it.ispwproject.findyourbook.dao.ConnectionFactory;
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

        // ConnectionFactory.changeRole() e' eager: apre subito una connessione
        // MySQL vera con le credenziali del ruolo appena autenticato. In
        // modalita' Memory il login non tocca mai il database (vedi
        // DAOFactory.getLoginDAO()/getUserDAO() sopra), quindi qui saltiamo
        // volutamente il cambio di ruolo: chiamarlo comunque proverebbe ad
        // aprire una connessione MySQL non necessaria, rompendo un login che
        // altrimenti funzionerebbe anche senza database raggiungibile.
        if (!DAOFactory.MEMORY.equalsIgnoreCase(DAOFactory.getPersistence())) {
            ConnectionFactory.changeRole(credentials.getRole());
        }

        return switch (credentials.getRole()) {
            case READER -> LoginResult.SUCCESSO_READER;
            case PUBLISHER -> LoginResult.SUCCESSO_PUBLISHER;
            default -> throw new IllegalStateException("Ruolo non riconosciuto: " + credentials.getRole());
        };
    }
}