package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.dao.ConnectionFactory;
import it.ispwproject.findyourbook.model.User;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.sql.SQLException;

public class MainGUI extends Application {

    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 800;

    private static Stage primaryStage;

    private static void setPrimaryStage(Stage stage) {
        MainGUI.primaryStage = stage;
    }

    @Override
    public void start(Stage stage) {
        setPrimaryStage(stage);
        stage.setTitle("Find Your Book");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/findyourbook_icon_256.png")));
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        stage.setMinWidth(WINDOW_WIDTH);
        stage.setMinHeight(WINDOW_HEIGHT);
        stage.setResizable(true);

        showLogin();
    }

    public static void showLogin() {
        new LoginGUI(primaryStage).show();
    }

    public static void showReaderDashboard() {
        User loggedUser = SessionManager.getInstance().getLoggedUser();
        if (loggedUser == null) {
            AppLogger.logError(" ERRORE: loggedUser è NULL! Reindirizzo al login.");
            showLogin();
            return;
        }

        if (!SessionManager.getInstance().isReader()) {
            AppLogger.logError(" ERRORE: accesso alla dashboard Lettore negato, ruolo non valido. Reindirizzo al login.");
            showLogin();
            return;
        }

        String displayName = loggedUser.getUsername();

        if (displayName == null || displayName.isEmpty()) {
            displayName = loggedUser.getName();
        }
        if (displayName == null || displayName.isEmpty()) {
            displayName = "Lettore";
        }

        Runnable onLogout = () -> {
            SessionManager.getInstance().clearSession();
            try {
                ConnectionFactory.clearRole();
            } catch (SQLException e) {
                AppLogger.logError("Errore durante il reset delle credenziali DB al logout: " + e.getMessage());
            }
            showLogin();
        };

        new ReaderDashboardGUI(primaryStage, displayName, onLogout).show();
    }

    public static void showPublisherDashboard() {

        User loggedUser = SessionManager.getInstance().getLoggedUser();
        if (loggedUser == null) {
            AppLogger.logError("ERRORE: loggedUser è NULL! Reindirizzo al login.");
            showLogin();
            return;
        }

        if (!SessionManager.getInstance().isPublisher()) {
            AppLogger.logError("ERRORE: accesso alla dashboard Casa Editrice negato, ruolo non valido. Reindirizzo al login.");
            showLogin();
            return;
        }

        String displayName = loggedUser.getUsername();

        if (displayName == null || displayName.isEmpty()) {
            displayName = loggedUser.getName();
        }
        if (displayName == null || displayName.isEmpty()) {
            displayName = "Casa Editrice";
        }

        Runnable onLogout = () -> {
            SessionManager.getInstance().clearSession();
            try {
                ConnectionFactory.clearRole();
            } catch (SQLException e) {
                AppLogger.logError("Errore durante il reset delle credenziali DB al logout: " + e.getMessage());
            }
            showLogin();
        };

        new PublisherDashboardGUI(primaryStage, displayName, onLogout).show();
    }

    public static void launch(String[] args) {
        Application.launch(MainGUI.class, args);
    }
}