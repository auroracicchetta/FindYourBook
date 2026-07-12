package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.model.User;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.application.Application;
import javafx.stage.Stage;

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

        String displayName = loggedUser.getUsername();

        if (displayName == null || displayName.isEmpty()) {
            displayName = loggedUser.getName();  // Fallback
        }
        if (displayName == null || displayName.isEmpty()) {
            displayName = "Lettore";
        }

        Runnable onLogout = () -> {
            SessionManager.getInstance().clearSession();
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

        String displayName = loggedUser.getUsername();

        if (displayName == null || displayName.isEmpty()) {
            displayName = loggedUser.getName();  // Fallback
        }
        if (displayName == null || displayName.isEmpty()) {
            displayName = "Casa Editrice";
        }

        Runnable onLogout = () -> {
            SessionManager.getInstance().clearSession();
            showLogin();
        };

        new PublisherDashboardGUI(primaryStage, displayName, onLogout).show();
    }

    public static void launch(String[] args) {
        Application.launch(MainGUI.class, args);
    }
}