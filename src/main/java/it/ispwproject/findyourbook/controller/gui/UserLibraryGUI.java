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
    private String currentFilter = "DA_LEGGERE"; // Ricorda in che sezione siamo


    public UserLibraryGUI(Stage stage) {
        this.stage = stage;
        this.view = new UserLibraryGUIView();
        this.bookController = new BookController(); // Istanziamo il controller applicativo
    }

    public void show() {
        Parent root = view.buildRoot(
                () -> new ReaderDashboardGUI(stage).show(), // <--- DEVE ESSERE ReaderDashboardGUI!
                MainGUI::showLogin,
                this::handleSearch,
                this::loadBooksByStatus
        );

        Scene scene = GUIUtils.createScene(root);
        stage.setScene(scene);
        stage.show();

        showWelcomeDashboard();

    }

    private void showWelcomeDashboard() {
        Platform.runLater(() -> {
            String username = SessionManager.getInstance().getLoggedUser().getUsername();
            javafx.scene.control.Label welcomeLabel = new javafx.scene.control.Label("Benvenuta nella tua area personale, " + username + "!\nSeleziona una categoria qui in alto per visualizzare i tuoi libri.");
            welcomeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #7A7A7A; -fx-font-style: italic; -fx-text-alignment: center;");

            javafx.scene.layout.VBox container = new javafx.scene.layout.VBox(welcomeLabel);
            container.setAlignment(javafx.geometry.Pos.CENTER);
            container.setPadding(new javafx.geometry.Insets(50, 0, 0, 0));

            List<javafx.scene.layout.VBox> defaultContent = new ArrayList<>();
            defaultContent.add(container);

            view.populateGrid(defaultContent);
        });
    }

    // --- IL METODO CHE CARICA I LIBRI DAL DATABASE ---
    private void loadBooksByStatus(String status) {
        this.currentFilter = status; // Salva la tab attuale!
        AppLogger.logInfo("Richiesti libri per lo stato: " + status);

        // Usiamo un Thread per non bloccare la grafica mentre interroghiamo il DB
        new Thread(() -> {
            try {
                // 1. Prendi l'utente attualmente loggato
                String username = SessionManager.getInstance().getLoggedUser().getUsername();

                // 2. Chiedi al BookController i libri per questo utente e stato
                List<BookBean> libriTrovati = bookController.getFavoriteBooks(username, status);

                // 3. Trasformiamo i BookBean in schede grafiche (BookCard)
                List<VBox> bookCards = new ArrayList<>();
                for (BookBean book : libriTrovati) {
                    VBox card = view.buildBookCard(
                            null,                      // 1. Titolo sezione
                            book.getTitle(),           // 2. Titolo libro
                            book.getAuthor(),          // 3. Autore
                            book.getImageUrl(),        // 4. Immagine
                            status,                    // 5. Stato (Da leggere, Letto...)
                            book.getRating(),          // 6. IL VOTO (initialRating)
                            newStatus -> changeBookStatus(book, newStatus), // 7. Azione Tendina
                            rating -> {                // 8. Azione Stelline
                                it.ispwproject.findyourbook.controller.applicativo.UserLibraryController appController =
                                        new it.ispwproject.findyourbook.controller.applicativo.UserLibraryController();
                                appController.rateBook(book, rating);
                                book.setRating(rating);
                            },
                            // --- RIGA CORRETTA ---
                            // Sostituisci la chiamata a BookDetailGUI con questa:
                            () -> new it.ispwproject.findyourbook.controller.gui.BookDetailGUI(stage, book, status,
                                    () -> new UserLibraryGUI(stage).show() // <--- Torna alla libreria!
                            ).show()
                    );
                    bookCards.add(card);
                }

                // 4. Aggiorniamo la UI
                Platform.runLater(() -> {
                    view.populateGrid(bookCards);
                });

            } catch (Exception e) {
                Platform.runLater(() -> AppLogger.logError("Errore caricamento libreria: " + e.getMessage()));
            }
        }).start();
    }

    // --- METODI DI SUPPORTO ---

    private void changeBookStatus(BookBean book, String newStatus) {
        AppLogger.logInfo("Richiesto spostamento del libro '" + book.getTitle() + "' in " + newStatus);

        // Usiamo un Thread per non bloccare la grafica mentre il DB lavora
        new Thread(() -> {
            try {
                // Istanziamo il Controller Applicativo (lo stesso che usi nella ricerca)
                it.ispwproject.findyourbook.controller.applicativo.UserLibraryController appController =
                        new it.ispwproject.findyourbook.controller.applicativo.UserLibraryController();

                if ("RIMUOVI".equals(newStatus)) {
                    // 1. Logica di rimozione
                    appController.removeBookFromLibrary(book);
                    AppLogger.logInfo("✅ Libro rimosso definitivamente dal Database.");
                } else {
                    // 2. Logica di salvataggio/aggiornamento
                    appController.saveBookToLibrary(book, newStatus);
                    AppLogger.logInfo("✅ Stato del libro aggiornato con successo a: " + newStatus);
                }


                Platform.runLater(() -> {
                    // Puoi richiamare il metodo di caricamento se tieni traccia dello stato attuale
                    loadBooksByStatus(currentFilter);
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
                // Usiamo il servizio di Google Books per cercare (come nella Home)
                it.ispwproject.findyourbook.service.GoogleBooksService googleService = new it.ispwproject.findyourbook.service.GoogleBooksService();
                List<BookBean> risultati = googleService.searchBooks(query);

                // Apriamo la schermata dei risultati passando la lista
                Platform.runLater(() -> {
                    new SearchResultsGUI(stage, risultati, query).show();
                });
            } catch (Exception e) {
                Platform.runLater(() -> AppLogger.logError("Errore ricerca: " + e.getMessage()));
            }
        }).start();
    }
}