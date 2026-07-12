package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.bean.PublisherStatsBean;
import it.ispwproject.findyourbook.controller.applicativo.PublisherStatsController;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import it.ispwproject.findyourbook.view.gui.PublisherStatsGUIView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PublisherStatsGUI {

    private final Stage stage;
    private final PublisherStatsGUIView view = new PublisherStatsGUIView();
    private final PublisherStatsController controller = new PublisherStatsController();
    private final String companyName;
    private final Runnable onLogout;

    public PublisherStatsGUI(Stage stage, String companyName, Runnable onLogout) {
        this.stage = stage;
        this.companyName = companyName;
        this.onLogout = onLogout;
    }

    public void show() {
        Parent root = view.buildRoot(companyName, this::goBack, onLogout);
        Scene scene = GUIUtils.createScene(root);

        loadRealData();

        stage.setScene(scene);
        stage.show();
    }

    private void goBack() {
        new PublisherDashboardGUI(stage, companyName, onLogout).show();
    }

    private void loadRealData() {
        try {
            String username = SessionManager.getInstance().getLoggedUser().getUsername();
            PublisherStatsBean stats = controller.getStatistics(username);
            view.updateView(stats);
        } catch (DAOException e) {
            AppLogger.logError("Errore nel caricamento delle statistiche: " + e.getMessage());
        }
    }
}