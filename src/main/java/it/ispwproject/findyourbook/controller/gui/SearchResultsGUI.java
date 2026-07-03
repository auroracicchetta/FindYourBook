package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.controller.applicativo.BookController;
import it.ispwproject.findyourbook.view.gui.SearchResultsGUIView;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.List;

public class SearchResultsGUI {
    private final Stage stage;
    private final List<BookBean> results;
    private final String lastQuery;
    private final SearchResultsGUIView view;

    public SearchResultsGUI(Stage stage, List<BookBean> results, String lastQuery) {
        this.stage = stage;
        this.results = results;
        this.lastQuery = lastQuery;
        this.view = new SearchResultsGUIView();
    }

    public void show() {
        Parent root = view.buildRoot(
                this.results,
                this.lastQuery,
                () -> new ReaderDashboardGUI(stage).show(),
                this::handleSearch,
                MainGUI::showLogin,
                () -> new UserLibraryGUI(stage).show(),

                book -> new it.ispwproject.findyourbook.controller.gui.BookDetailGUI(stage, book, book.getStatus(),
                        () -> new SearchResultsGUI(stage, this.results, this.lastQuery).show()
                ).show()
        );

        Scene scene = GUIUtils.createScene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void handleSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;
        new Thread(() -> {
            try {
                // Anche qui, usiamo il BookController per avere la sincronizzazione
                BookController controller = new BookController();
                List<BookBean> risultati = controller.searchBooks(query);

                Platform.runLater(() -> new SearchResultsGUI(stage, risultati, query).show());
            } catch (Exception e) {
                // Risolto il code smell usando il logger al posto di System.err
                Platform.runLater(() -> AppLogger.logError("Errore ricerca: " + e.getMessage()));
            }
        }).start();
    }
}