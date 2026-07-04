package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.controller.applicativo.BookController;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
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
    private String currentFilter = "DA_LEGGERE";


    public UserLibraryGUI(Stage stage) {
        this.stage = stage;
        this.view = new UserLibraryGUIView();
        this.bookController = new BookController();
    }

    public void show() {
        // 1. Recuperiamo Nome Utente e Conteggio dei Libri Letti
        String username = SessionManager.getInstance().getLoggedUser().getUsername();
        int readCount = 0;
        try {
            // Contiamo quanti elementi ci sono nella lista dei "LETTI"
            readCount = bookController.getFavoriteBooks(username, "LETTO").size();
        } catch (Exception e) {
            AppLogger.logWarning("Impossibile recuperare il conteggio dei libri letti.");
        }

        Parent root = view.buildRoot(
                username,                                   // Passiamo il nome alla GUI!
                readCount,                                  // Passiamo il conteggio reale alla GUI!
                () -> new ReaderDashboardGUI(stage).show(),
                MainGUI::showLogin,
                this::handleSearch,
                this::loadBooksByStatus
        );

        Scene scene = GUIUtils.createScene(root);
        stage.setScene(scene);
        stage.show();

        // Eliminiamo il vecchio messaggio al centro dello schermo e carichiamo subito i libri!
        loadBooksByStatus(currentFilter);
    }

    private void loadBooksByStatus(String status) {
        this.currentFilter = status;
        AppLogger.logInfo("Richiesti libri per lo stato: " + status);

        new Thread(() -> {
            try {
                String username = SessionManager.getInstance().getLoggedUser().getUsername();
                List<BookBean> libriTrovati = bookController.getFavoriteBooks(username, status);
                List<VBox> bookCards = new ArrayList<>();

                for (BookBean book : libriTrovati) {
                    VBox card = view.buildBookCard(
                            null,
                            book,
                            status,
                            newStatus -> changeBookStatus(book, newStatus),
                            rating -> {
                                it.ispwproject.findyourbook.controller.applicativo.UserLibraryController appController =
                                        new it.ispwproject.findyourbook.controller.applicativo.UserLibraryController();
                                appController.rateBook(book, rating);
                                book.setRating(rating);
                            },
                            () -> new it.ispwproject.findyourbook.controller.gui.BookDetailGUI(stage, book, status,
                                    () -> new UserLibraryGUI(stage).show()
                            ).show()
                    );
                    bookCards.add(card);
                }

                Platform.runLater(() -> {
                    view.populateGrid(bookCards);
                });

            } catch (Exception e) {
                Platform.runLater(() -> AppLogger.logError("Errore caricamento libreria: " + e.getMessage()));
            }
        }).start();
    }

    private void changeBookStatus(BookBean book, String newStatus) {
        AppLogger.logInfo("Richiesto spostamento del libro '" + book.getTitle() + "' in " + newStatus);

        new Thread(() -> {
            try {
                it.ispwproject.findyourbook.controller.applicativo.UserLibraryController appController =
                        new it.ispwproject.findyourbook.controller.applicativo.UserLibraryController();

                if ("RIMUOVI".equals(newStatus)) {
                    appController.removeBookFromLibrary(book);
                    AppLogger.logInfo("✅ Libro rimosso definitivamente dal Database.");
                } else {
                    appController.saveBookToLibrary(book, newStatus);
                    AppLogger.logInfo("✅ Stato del libro aggiornato con successo a: " + newStatus);
                }

                Platform.runLater(() -> {
                    // Quando cambi stato, ricarichiamo interamente la GUI per assicurarci
                    // che anche i contatori si aggiornino in tempo reale!
                    new UserLibraryGUI(stage).show();
                });

            } catch (Exception e) {
                AppLogger.logError("❌ Errore durante la comunicazione con il Database: " + e.getMessage());
            }
        }).start();
    }

    private void handleSearch(String query) {
        AppLogger.logInfo("Ricerca avviata da MyBooks per: " + query);
        if (query == null || query.trim().isEmpty()) return;

        new Thread(() -> {
            try {
                // Passiamo obbligatoriamente per il BookController in modo da non rompere mai più le sincronizzazioni!
                it.ispwproject.findyourbook.controller.applicativo.BookController controller =
                        new it.ispwproject.findyourbook.controller.applicativo.BookController();

                List<it.ispwproject.findyourbook.bean.BookBean> risultati = controller.searchBooks(query);

                Platform.runLater(() -> {
                    new SearchResultsGUI(stage, risultati, query).show();
                });
            } catch (Exception e) {
                Platform.runLater(() -> AppLogger.logError("Errore ricerca: " + e.getMessage()));
            }
        }).start();
    }
}