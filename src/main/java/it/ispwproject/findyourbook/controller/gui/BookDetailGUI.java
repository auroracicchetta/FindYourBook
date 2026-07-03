package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.view.gui.BookDetailGUIView;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import it.ispwproject.findyourbook.util.logger.AppLogger;

import java.util.List;

public class BookDetailGUI {

    private final Stage stage;
    private final BookBean book;
    private final String currentStatus;
    private final Runnable onBackAction; // <--- NUOVO: L'azione personalizzata per tornare indietro
    private final BookDetailGUIView view;

    // Aggiungiamo onBackAction al costruttore
    public BookDetailGUI(Stage stage, BookBean book, String currentStatus, Runnable onBackAction) {
        this.stage = stage;
        this.book = book;
        this.currentStatus = currentStatus;
        this.onBackAction = onBackAction; // Lo salviamo
        this.view = new BookDetailGUIView();
    }

    public void show() {
        Parent root = view.buildRoot(
                this.book,
                this.currentStatus,
                this.onBackAction,                            // <--- USIAMO L'AZIONE DINAMICA QUI!
                () -> new ReaderDashboardGUI(stage).show(),
                () -> new UserLibraryGUI(stage).show(),
                MainGUI::showLogin,
                this::handleSearch
        );

        Scene scene = GUIUtils.createScene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Aggiungiamo il metodo per far funzionare la barra di ricerca anche da qui
    private void handleSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;

        new Thread(() -> {
            try {
                it.ispwproject.findyourbook.service.GoogleBooksService googleService =
                        new it.ispwproject.findyourbook.service.GoogleBooksService();
                List<BookBean> risultati = googleService.searchBooks(query);

                Platform.runLater(() -> {
                    new SearchResultsGUI(stage, risultati, query).show();
                });
            } catch (Exception e) {
                Platform.runLater(() -> AppLogger.logError("Errore ricerca: " + e.getMessage()));
            }
        }).start();
    }
}