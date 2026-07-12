package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.controller.applicativo.BookController;
import it.ispwproject.findyourbook.controller.applicativo.UserLibraryController;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import it.ispwproject.findyourbook.view.gui.BookDetailGUIView;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class BookDetailGUI {

    private final Stage stage;
    private final String username;
    private final Runnable onLogout;
    private final BookBean book;
    private final ReadingStatus currentStatus;
    private final Runnable onBackAction;
    private final BookDetailGUIView view;
    private final BookController bookController;
    private final UserLibraryController userLibraryController;

    public BookDetailGUI(Stage stage, String username, Runnable onLogout, BookBean book, ReadingStatus currentStatus, Runnable onBack) {
        this.stage = stage;
        this.username = username;
        this.onLogout = onLogout;
        this.book = book;
        this.currentStatus = currentStatus;
        this.onBackAction = onBack;
        this.view = new BookDetailGUIView();
        this.bookController = new BookController();
        this.userLibraryController = new UserLibraryController();
    }

    public void show() {
        Parent root = view.buildRoot(
                this.username,
                this.book,
                this.currentStatus,
                this::handleStatusChange,
                rating -> {
                    try {
                        new UserLibraryController().rateBook(this.book, rating);
                        this.book.setRating(rating);
                    } catch (Exception e) {
                        AppLogger.logError("Errore salvataggio valutazione: " + e.getMessage());
                    }
                },
                this.onBackAction,
                () -> new ReaderDashboardGUI(stage, this.username, this.onLogout).show(),
                () -> new UserLibraryGUI(stage, this.username, this.onLogout).show(),
                this.onLogout,
                this::handleSearch
        );

        Scene scene = GUIUtils.createScene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void handleStatusChange(String selectedOption) {
        try {
            if (selectedOption == null || selectedOption.equals("Rimuovi libro") || selectedOption.equals("RIMUOVI")) {
                userLibraryController.removeBookFromLibrary(this.book);
                this.book.setStatus(null);
            } else {
                ReadingStatus newStatus = null;
                if (selectedOption.equals(ReadingStatus.TO_READ.name()) || selectedOption.equals(ReadingStatus.TO_READ.getDisplayName())) {
                    newStatus = ReadingStatus.TO_READ;
                } else if (selectedOption.equals(ReadingStatus.READING.name()) || selectedOption.equals(ReadingStatus.READING.getDisplayName())) {
                    newStatus = ReadingStatus.READING;
                } else if (selectedOption.equals(ReadingStatus.READ.name()) || selectedOption.equals(ReadingStatus.READ.getDisplayName())) {
                    newStatus = ReadingStatus.READ;
                } else {
                    newStatus = ReadingStatus.valueOf(selectedOption);
                }

                userLibraryController.saveBookToLibrary(this.book, newStatus);
                this.book.setStatus(newStatus);
            }
        } catch (Exception e) {
            AppLogger.logError("Errore aggiornamento libreria: " + e.getMessage());
        }
    }

    private void handleSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;

        try {
            List<BookBean> risultati = bookController.searchBooks(query);
            userLibraryController.syncBooksWithDatabase(risultati);
            new SearchResultsGUI(stage, this.username, this.onLogout, risultati, query).show();
        } catch (Exception e) {
            AppLogger.logError("Errore ricerca: " + e.getMessage());
        }
    }
}