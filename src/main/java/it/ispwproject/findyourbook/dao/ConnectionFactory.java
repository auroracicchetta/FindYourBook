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

    private static Connection connection;
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

    private static void initConnection() throws SQLException {
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

        connection = DriverManager.getConnection(url, user, pass);
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initConnection();
        }
        return connection;
    }

    public static synchronized void changeRole(Role role) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        currentRole = role;
        initConnection();
    }

    public static synchronized void clearRole() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        currentRole = null;
    }
}