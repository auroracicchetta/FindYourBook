package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.controller.applicativo.UserLibraryController;
import it.ispwproject.findyourbook.view.gui.ReaderDashboardGUIView;
import it.ispwproject.findyourbook.controller.applicativo.BookController;
import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class ReaderDashboardGUI {

    private final Stage stage;
    private final ReaderDashboardGUIView view;
    private final BookController bookController;
    private final String username;
    private final Runnable onLogout;

    public ReaderDashboardGUI(Stage stage, String username, Runnable onLogout) {
        this.stage = stage;
        this.username = username;
        this.onLogout = onLogout;
        this.view = new ReaderDashboardGUIView();
        this.bookController = new BookController();
    }

    public void show() {
        Parent root = view.buildRoot(
                username,
                onLogout,
                () -> new UserLibraryGUI(stage, username, onLogout).show(),
                this::handleSearch,
                this::handleGenreSelection
        );

        Scene scene = GUIUtils.createScene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void handleGenreSelection(String genre) {
        AppLogger.logInfo("Ricerca per genere: " + genre);
        try {
            List<BookBean> risultati = bookController.getBooksByGenre(genre);
            new UserLibraryController().syncBooksWithDatabase(risultati);

            AppLogger.logInfo("Generi caricati! Trovati " + risultati.size() + " libri.");
            new SearchResultsGUI(stage, username, onLogout, risultati, genre).show();

        } catch (Exception e) {
            AppLogger.logError("Errore nel recupero genere: " + e.getMessage());
        }
    }

    private void handleSearch(String query) {

        if (query == null || query.trim().isEmpty()) {
            return;
        }

        AppLogger.logInfo("Ricerca avviata per: " + query);

        try {
            List<BookBean> risultati = bookController.searchBooks(query);
            new UserLibraryController().syncBooksWithDatabase(risultati);

            AppLogger.logInfo("Ricerca completata! Trovati " + risultati.size() + " libri.");
            new SearchResultsGUI(stage, username, onLogout, risultati, query).show();

        } catch (Exception e) {
            AppLogger.logError("Errore durante la ricerca: " + e.getMessage());
        }
    }
}