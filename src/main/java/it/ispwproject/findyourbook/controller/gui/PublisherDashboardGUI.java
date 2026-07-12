package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.model.User;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.view.gui.PublisherDashboardGUIView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public class PublisherDashboardGUI {

    private final Stage stage;
    private final PublisherDashboardGUIView view = new PublisherDashboardGUIView();
    private final String companyName;
    private final Runnable onLogout;

    public PublisherDashboardGUI(Stage stage, String companyName, Runnable onLogout) {
        this.stage = stage;
        this.companyName = companyName;
        this.onLogout = onLogout;
    }

    public void show() {

        Parent root = view.buildRoot(
                companyName,
                this::handleLogout,
                this::handleViewCatalog,
                this::handlePublishNewBook,
                this::handleViewStats
        );

        Scene scene = GUIUtils.createScene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void handleLogout() {
        AppLogger.logInfo("Logout Casa Editrice ed eliminazione sessione...");
        SessionManager.getInstance().clearSession();
        MainGUI.showLogin();
    }

    private void handleViewCatalog() {
        AppLogger.logInfo("Apertura pagina del catalogo...");
        new PublisherCatalogGUI(stage, companyName, onLogout).show();
    }

    private void handlePublishNewBook() {
        AppLogger.logInfo("Apertura form di pubblicazione nuovo libro...");
        new PublishBookGUI(stage, companyName, onLogout).show();
    }

    private void handleViewStats() {
        AppLogger.logInfo("Apertura pannello statistiche vendite corporate...");
        new PublisherStatsGUI(stage, companyName, onLogout).show();
    }
}