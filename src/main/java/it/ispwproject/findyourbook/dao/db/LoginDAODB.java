package it.ispwproject.findyourbook.dao.db;

// import it.ispwproject.findyourbook.dao.ConnectionFactory; <-- Lo useremo quando configureremo il DB vero!
import it.ispwproject.findyourbook.dao.LoginDAO;
import it.ispwproject.findyourbook.exception.LoginException;
import it.ispwproject.findyourbook.model.Credentials;

public class LoginDAODB implements LoginDAO {

    @Override
    public Credentials execute(String username, String plainPassword) throws LoginException {
        // PER ORA: Lasciamo questo metodo "vuoto" in attesa di creare la ConnectionFactory per MySQL.
        // Se si attiva la modalità "DATABASE", lancerà questa eccezione temporanea.
        throw new LoginException("Connessione al database reale non ancora configurata!");
    }
}