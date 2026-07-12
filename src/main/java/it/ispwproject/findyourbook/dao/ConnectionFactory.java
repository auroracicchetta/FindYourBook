package it.ispwproject.findyourbook.dao;

import java.io.FileInputStream;
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

    static {
        try (InputStream input = ConnectionFactory.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new Exception("File db.properties non trovato nel classpath!");
            }
            props.load(input);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Errore nel caricamento del file db.properties: " + e.getMessage());
        }
    }

    // Sostituita l'eccezione generica con SQLException
    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                props.getProperty("CONNECTION_URL"),
                props.getProperty("LOGIN_USER"),
                props.getProperty("LOGIN_PASS")
        );
    }
}