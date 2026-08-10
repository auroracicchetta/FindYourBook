package it.ispwproject.findyourbook.dao;

import it.ispwproject.findyourbook.enumerator.Role;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {

    private ConnectionFactory() {
        throw new IllegalStateException("Classe di utilità");
    }

    private static final Properties props = new Properties();

    private static Role currentRole;

    static {
        try (InputStream input = ConnectionFactory.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IllegalStateException("File db.properties non trovato nel classpath!");
            }
            props.load(input);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Errore nel caricamento del file db.properties: " + e.getMessage());
        }
    }

    // Prima questo metodo restituiva sempre la STESSA connessione statica
    // condivisa, riaperta solo se risultava chiusa. I DAO la chiudono sempre
    // in try-with-resources a fine query: va bene se le chiamate sono
    // sequenziali, ma se due thread (es. il fork+join di UserLibraryCLI/GUI,
    // fatto apposta per rispecchiare il vero parallelismo dell'Activity
    // Diagram) la usano insieme, uno la chiude mentre l'altro la sta ancora
    // usando ("No operations allowed after connection closed"). Ora ogni
    // chiamata apre una connessione MySQL nuova e indipendente: costa una
    // connessione fisica in piu' per query invece di riusarne una sola, ma
    // elimina la race condition e permette al fork+join di restare davvero
    // parallelo senza rischi.
    public static synchronized Connection getConnection() throws SQLException {
        String url = props.getProperty("CONNECTION_URL");
        String user;
        String pass;

        if (currentRole != null) {
            user = props.getProperty(currentRole.name() + "_USER");
            pass = props.getProperty(currentRole.name() + "_PASS");
        } else {
            user = props.getProperty("LOGIN_USER");
            pass = props.getProperty("LOGIN_PASS");
        }

        if (user == null || pass == null) {
            throw new SQLException("Credenziali mancanti per il ruolo: " + currentRole);
        }

        return DriverManager.getConnection(url, user, pass);
    }

    public static synchronized void changeRole(Role role){
        currentRole = role;
    }

    public static synchronized void clearRole(){
        currentRole = null;
    }
}