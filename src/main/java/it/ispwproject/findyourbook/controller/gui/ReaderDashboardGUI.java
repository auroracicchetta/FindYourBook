package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.view.gui.ReaderDashboardGUIView;
import it.ispwproject.findyourbook.controller.applicativo.BookController;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.util.List;
import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public class ReaderDashboardGUI {

    private final Stage stage;
    private final ReaderDashboardGUIView view;

    public ReaderDashboardGUI(Stage stage) {
        this.stage = stage;
        this.view = new ReaderDashboardGUIView();
    }

    public void show() {
        javafx.scene.Parent root = view.buildRoot(
                MainGUI::showLogin,
                () -> new UserLibraryGUI(stage).show(),
                this::handleSearch,
                this::handleGenreSelection
        );

        javafx.scene.Scene scene = GUIUtils.createScene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void handleGenreSelection(String genere) {
        AppLogger.logInfo("Ricerca per genere: " + genere);

        new Thread(() -> {
            try {
                // 1. Usa il BookController che hai già (che gestisce API + DB)
                BookController controller = new BookController();
                List<BookBean> risultati = controller.getBooksByGenre(genere);

                // 2. Aggiorna la UI sul Thread grafico
                Platform.runLater(() -> {
                    AppLogger.logInfo("Generi caricati! Trovati " + risultati.size() + " libri.");
                    new SearchResultsGUI(stage, risultati, genere).show();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    AppLogger.logError("Errore nel recupero genere: " + e.getMessage());
                });
            }
        }).start();
    }

    private void handleSearch(String query) {
        AppLogger.logInfo("Ricerca avviata per: " + query);

        if (query == null || query.trim().isEmpty()) {
            return;
        }

        new Thread(() -> {
            try {
                // 1. Chiamiamo il controller (che ora fa sia la ricerca che la sincronizzazione stelline)
                BookController controller = new BookController();
                List<BookBean> risultati = controller.searchBooks(query);

                // 2. Aggiorniamo la UI passando i risultati
                Platform.runLater(() -> {
                    AppLogger.logInfo("Ricerca completata! Trovati " + risultati.size() + " libri.");
                    new SearchResultsGUI(stage, risultati, query).show();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    AppLogger.logError("Errore durante la ricerca: " + e.getMessage());
                });
            }
        }).start();
    }
}