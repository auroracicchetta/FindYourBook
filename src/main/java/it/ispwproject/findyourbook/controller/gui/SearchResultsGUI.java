package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.controller.applicativo.BookController;
import it.ispwproject.findyourbook.controller.applicativo.UserLibraryController;
import it.ispwproject.findyourbook.view.gui.SearchResultsGUIView;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class SearchResultsGUI {
    private final Stage stage;
    private final String username;
    private final Runnable onLogout;
    private final List<BookBean> results;
    private final String lastQuery;
    private final SearchResultsGUIView view;
    private final BookController bookController;
    private final UserLibraryController userLibraryController;

    public SearchResultsGUI(Stage stage, String username, Runnable onLogout, List<BookBean> results, String lastQuery) {
        this.stage = stage;
        this.username = username;
        this.onLogout = onLogout;
        this.results = results;
        this.lastQuery = lastQuery;
        this.view = new SearchResultsGUIView();
        this.bookController = new BookController();
        this.userLibraryController = new UserLibraryController();
    }


    public void show() {

        Parent root = view.buildRoot(
                this.username,
                this.results,
                this.lastQuery,
                () -> new ReaderDashboardGUI(stage, this.username, onLogout).show(),
                this::handleSearch,
                onLogout,
                () -> new UserLibraryGUI(stage, this.username, onLogout).show(),
                book -> new BookDetailGUI(stage, this.username, onLogout, book, book.getStatus(),
                        () -> new SearchResultsGUI(stage, this.username, onLogout, this.results, this.lastQuery).show()
                ).show(),

                (book, newStatus) -> {
                    try {
                        UserLibraryController libController = new UserLibraryController();
                        if (newStatus == null) {
                            libController.removeBookFromLibrary(book);
                            book.setStatus(null);
                        } else {
                            libController.saveBookToLibrary(book, newStatus);
                            book.setStatus(newStatus);
                        }
                        AppLogger.logInfo("Stato griglia aggiornato per: " + book.getTitle());
                    } catch (Exception e) {
                        AppLogger.logError("Errore salvataggio stato da griglia: " + e.getMessage());
                    }
                },

                (book, rating) -> {
                    try {
                        userLibraryController.rateBook(book, rating);
                        book.setRating(rating);
                        AppLogger.logInfo("Voto griglia aggiornato per: " + book.getTitle());
                    } catch (Exception e) {
                        AppLogger.logError("Errore valutazione da griglia: " + e.getMessage());
                    }
                }
        );

        Scene scene = GUIUtils.createScene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void handleSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;

        try {
            List<BookBean> risultati = bookController.searchBooks(query);
            new UserLibraryController().syncBooksWithDatabase(risultati);

            new SearchResultsGUI(stage, this.username, onLogout, risultati, query).show();
        } catch (Exception e) {
            AppLogger.logError("Errore ricerca: " + e.getMessage());
        }
    }
}