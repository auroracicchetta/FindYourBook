package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.controller.applicativo.BookController;
import it.ispwproject.findyourbook.controller.applicativo.UserLibraryController;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import it.ispwproject.findyourbook.model.User;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.service.NotificationService;
import it.ispwproject.findyourbook.view.gui.UserLibraryGUIView;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class UserLibraryGUI {

    private final Stage stage;
    private final UserLibraryGUIView view;
    private final BookController bookController;
    private final UserLibraryController userLibraryController;
    private final String username;
    private final Runnable onLogout;
    private static ReadingStatus currentFilter = null;

    public UserLibraryGUI(Stage stage, String username, Runnable onLogout) {
        this.stage = stage;
        this.username = username;
        this.onLogout = onLogout;
        this.view = new UserLibraryGUIView();
        this.bookController = new BookController();
        this.userLibraryController = new UserLibraryController();
    }

    public void show() {
        User loggedUser = SessionManager.getInstance().getLoggedUser();
        String username = loggedUser.getUsername();
        int readCount = 0;
        try {
            readCount = bookController.getFavoriteBooks(username, ReadingStatus.READ).size();
        } catch (Exception e) {
            AppLogger.logWarning("Impossibile recuperare il conteggio dei libri letti.");
        }

        userLibraryController.checkInactiveReading();

        Parent root = view.buildRoot(
                username,
                readCount,
                () -> new ReaderDashboardGUI(stage, username, onLogout).show(),
                onLogout,
                this::handleSearch,
                this::loadBooksByStatus
        );


        Scene scene = GUIUtils.createScene(root);
        stage.setScene(scene);
        stage.show();

        loadBooksByStatus(currentFilter);

    }

    private void loadBooksByStatus(ReadingStatus status) {

        this.currentFilter = status;
        view.setActiveButton(status);
        AppLogger.logInfo("Richiesti libri per lo stato: " + status.name());

        try {
            String username = SessionManager.getInstance().getLoggedUser().getUsername();
            List<BookBean> libriTrovati = bookController.getFavoriteBooks(username, status);
            List<VBox> bookCards = new ArrayList<>();

            for (BookBean book : libriTrovati) {
                VBox card = view.buildBookCard(
                        null,
                        book,
                        status.name(),
                        newStatus -> changeBookStatus(book, newStatus),
                        rating -> {
                            try {
                                userLibraryController.rateBook(book, rating);
                                book.setRating(rating);
                            } catch (Exception e) {
                                AppLogger.logError("Errore nel salvataggio del voto.");
                            }
                        },
                        () -> new BookDetailGUI(stage, username, onLogout, book, status,
                                () -> new UserLibraryGUI(stage, username, onLogout).show()
                        ).show()
                );
                bookCards.add(card);
            }

            view.populateGrid(bookCards);

        } catch (Exception e) {
            AppLogger.logError("Errore caricamento libreria: " + e.getMessage());
        }
    }

    private void changeBookStatus(BookBean book, String newStatus) {
        AppLogger.logInfo("Richiesto spostamento del libro '" + book.getTitle() + "' in " + newStatus);

        try {
            User loggedUser = SessionManager.getInstance().getLoggedUser();

            if ("Rimuovi libro".equals(newStatus) || "RIMUOVI".equals(newStatus)) {
                userLibraryController.removeBookFromLibrary(book);
                book.setStatus(null);
                AppLogger.logInfo("✅ Libro rimosso definitivamente dal Database.");
            } else {
                ReadingStatus targetStatus = null;
                if (newStatus.equals(ReadingStatus.TO_READ.getDisplayName()) || newStatus.equals(ReadingStatus.TO_READ.name())) {
                    targetStatus = ReadingStatus.TO_READ;
                } else if (newStatus.equals(ReadingStatus.READING.getDisplayName()) || newStatus.equals(ReadingStatus.READING.name())) {
                    targetStatus = ReadingStatus.READING;
                } else if (newStatus.equals(ReadingStatus.READ.getDisplayName()) || newStatus.equals(ReadingStatus.READ.name())) {
                    targetStatus = ReadingStatus.READ;
                }

                if (targetStatus != null) {
                    userLibraryController.saveBookToLibrary(book, targetStatus);
                    book.setStatus(targetStatus);
                    AppLogger.logInfo("✅ Stato aggiornato a: " + targetStatus.name());

                    if (targetStatus == ReadingStatus.READ) {
                        NotificationService.sendReadingGoalReachedNotification(
                                loggedUser.getEmail(),
                                loggedUser.getName(),
                                book.getTitle()
                        );
                    }
                }
            }

            this.show();

        } catch (Exception e) {
            AppLogger.logError("Errore durante la comunicazione con il Database: " + e.getMessage());
        }
    }


    private void handleSearch(String query) {
        AppLogger.logInfo("Ricerca avviata da MyBooks per: " + query);
        if (query == null || query.trim().isEmpty()) return;

        try {
            List<BookBean> risultati = bookController.searchBooks(query);
            userLibraryController.syncBooksWithDatabase(risultati);
            new SearchResultsGUI(stage, username, onLogout, risultati, query).show();

        } catch (Exception e) {
            AppLogger.logError("Errore ricerca: " + e.getMessage());
        }
    }
}