package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.controller.applicativo.BookController;
import it.ispwproject.findyourbook.controller.applicativo.UserLibraryController;
import it.ispwproject.findyourbook.dao.ConnectionFactory;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import it.ispwproject.findyourbook.view.gui.UserLibraryGUIView;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class UserLibraryGUI {

    private final Stage stage;
    private final UserLibraryGUIView view;
    private final BookController bookController;
    private final UserLibraryController userLibraryController;
    private final String username;
    private final Runnable onLogout;
    private ReadingStatus currentFilter = null;

    public UserLibraryGUI(Stage stage, String username, Runnable onLogout) {
        this.stage = stage;
        this.username = username;
        this.onLogout = onLogout;
        this.view = new UserLibraryGUIView();
        this.bookController = new BookController();
        this.userLibraryController = new UserLibraryController();
    }

    public void show() {
        // "Retrieve total read count" e "Check inactive READING books" leggono dati
        // indipendenti (libri READ vs libri READING) dallo stesso store: non c'e'
        // motivo per cui una debba aspettare l'altra. Vengono quindi eseguite in
        // parallelo (fork) e si attende il completamento di ENTRAMBE (join) prima
        // di costruire la UI, invece di eseguirle in sequenza come prima.
        CompletableFuture<Integer> readCountFuture = CompletableFuture.supplyAsync(() -> {
            synchronized (ConnectionFactory.class) {
                try {
                    return bookController.getFavoriteBooks(this.username, ReadingStatus.READ).size();
                } catch (Exception e) {
                    AppLogger.logWarning("Impossibile recuperare il conteggio dei libri letti.");
                    return 0;
                }
            }
        });

        CompletableFuture<Void> inactiveReadingFuture = CompletableFuture.runAsync(() -> {
            synchronized (ConnectionFactory.class) {
                userLibraryController.checkInactiveReading();
            }
        });

        // Join: il thread della GUI attende che entrambi i task siano completati
        // prima di proseguire con la costruzione della vista.
        CompletableFuture.allOf(readCountFuture, inactiveReadingFuture).join();
        int readCount = readCountFuture.join();

        Parent root = view.buildRoot(
                this.username,
                readCount,
                () -> new ReaderDashboardGUI(stage, this.username, onLogout).show(),
                onLogout,
                this::handleSearch,
                this::loadBooksByStatus
        );

        Scene scene = GUIUtils.createScene(root);
        stage.setScene(scene);
        stage.show();

        if (currentFilter != null) {
            loadBooksByStatus(currentFilter);
        } else {
            view.setActiveButton(null);
            view.showChooseSectionPrompt();
        }
    }

    private void updateCurrentFilter(ReadingStatus status) {
        currentFilter = status;
    }

    private void loadBooksByStatus(ReadingStatus status) {
        updateCurrentFilter(status);
        view.setActiveButton(status);
        AppLogger.logInfo("Richiesti libri per lo stato: " + status.name());

        try {
            List<BookBean> libriTrovati = bookController.getFavoriteBooks(this.username, status);
            List<VBox> bookCards = new ArrayList<>();

            for (BookBean book : libriTrovati) {
                VBox card = view.buildBookCard(
                        book,
                        status,
                        newStatus -> changeBookStatus(book, newStatus),
                        () -> confirmRemovalWithCountdown(book),
                        rating -> {
                            try {
                                userLibraryController.rateBook(book, rating);
                                book.setRating(rating);
                                this.show();
                            } catch (Exception e) {
                                AppLogger.logError("Errore nel salvataggio del voto.");
                            }
                        },
                        () -> new BookDetailGUI(stage, this.username, onLogout, book, status,
                                () -> new UserLibraryGUI(stage, this.username, onLogout).show(),
                                "a I miei libri"
                        ).show()
                );
                bookCards.add(card);
            }

            view.populateGrid(bookCards);

        } catch (Exception e) {
            AppLogger.logError("Errore caricamento libreria: " + e.getMessage());
        }
    }

    private void changeBookStatus(BookBean book, ReadingStatus newStatus) {
        AppLogger.logInfo("Richiesto spostamento del libro '" + book.getTitle() + "' in " + newStatus);

        try {
            userLibraryController.saveBookToLibrary(book, newStatus);
            this.show();

        } catch (Exception e) {
            AppLogger.logError("Errore durante la comunicazione con il Database: " + e.getMessage());
        }
    }

    // Timer vero: il countdown corre in parallelo
    // all'attesa di un click su "Rimuovi ora" o "Annulla". Se il tempo scade
    // senza un click esplicito su "Rimuovi ora", la richiesta viene annullata
    // di default (deny-by-default): solo una conferma esplicita porta a termine l'azione.
    private void confirmRemovalWithCountdown(BookBean book) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Rimuovi Libro");
        alert.setHeaderText("Rimozione di '" + book.getTitle() + "'");

        ButtonType btnRimuoviOra = new ButtonType("Rimuovi ora", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnRimuoviOra, btnAnnulla);

        Button removeButtonNode = (Button) alert.getDialogPane().lookupButton(btnRimuoviOra);
        removeButtonNode.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold;");

        final int[] secondsLeft = {5};
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        Runnable updateCountdown = () -> alert.setContentText(
                "Hai " + secondsLeft[0] + " secondi per confermare.\n" +
                        "Premi \"Rimuovi ora\" per confermare la rimozione, altrimenti la richiesta verra' annullata automaticamente."
        );
        updateCountdown.run();

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft[0]--;
            updateCountdown.run();
            if (secondsLeft[0] <= 0) {
                timeline.stop();
                alert.close();
            }
        });
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();

        Optional<ButtonType> result = alert.showAndWait();
        timeline.stop();

        if (result.isPresent() && result.get() == btnRimuoviOra) {
            try {
                userLibraryController.removeBookFromLibrary(book);
                AppLogger.logInfo("Libro '" + book.getTitle() + "' rimosso su conferma esplicita dell'utente.");
                this.show();
            } catch (Exception e) {
                AppLogger.logError("Errore durante la rimozione: " + e.getMessage());
            }
        } else {
            AppLogger.logInfo("Rimozione di '" + book.getTitle() + "' non confermata (annullata o tempo scaduto): il libro resta nella libreria.");
        }
    }


    private void handleSearch(String query) {
        AppLogger.logInfo("Ricerca avviata da MyBooks per: " + query);
        if (query == null || query.trim().isEmpty()) return;

        try {
            List<BookBean> risultati = bookController.searchBooks(query);
            userLibraryController.syncBooksWithDatabase(risultati);
            new SearchResultsGUI(stage, this.username, onLogout, risultati, query).show();

        } catch (Exception e) {
            AppLogger.logError("Errore ricerca: " + e.getMessage());
        }
    }
}