package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.controller.applicativo.PublisherController;
import it.ispwproject.findyourbook.model.User;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.view.gui.PublisherCatalogGUIView;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import it.ispwproject.findyourbook.util.logger.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class PublisherCatalogGUI {

    private final Stage stage;
    private final PublisherCatalogGUIView view = new PublisherCatalogGUIView();
    private final PublisherController appController = new PublisherController();
    private List<BookBean> allBooks = new ArrayList<>();
    private final String companyName;  // <--- AGGIUNTO
    private final Runnable onLogout;   // <--- AGGIUNTO

    // ✅ AGGIUNGI companyName e onLogout al costruttore
    public PublisherCatalogGUI(Stage stage, String companyName, Runnable onLogout) {
        this.stage = stage;
        this.companyName = companyName;
        this.onLogout = onLogout;
    }

    public void show() {
        // ✅ PASSAGGIO PARAMETRI CORRETTO!
        Scene scene = GUIUtils.createScene(view.buildRoot(
                companyName,
                this::handleBack,
                onLogout,
                this::filterCatalog
        ));

        loadPublisherCatalog();

        stage.setScene(scene);
        stage.show();
    }

    private void handleBack() {
        AppLogger.logInfo("Ritorno alla Dashboard principale...");
        new PublisherDashboardGUI(stage, companyName, onLogout).show();
    }

    private void loadPublisherCatalog() {
        try {
            this.allBooks = appController.getMyCatalog();
            displayBooks(this.allBooks);
        } catch (Exception e) {
            AppLogger.logError("Impossibile caricare il catalogo: " + e.getMessage());
        }
    }

    private void displayBooks(List<BookBean> booksToDisplay) {
        view.clearGrid();
        for (BookBean book : booksToDisplay) {
            view.addCatalogCard(
                    book.getTitle(),
                    book.getAuthor(),
                    book.getImageUrl(),
                    book.getCopieVendute(),
                    () -> openEditPopup(book)
            );
        }
    }

    private void filterCatalog(String query) {
        if (query == null || query.trim().isEmpty()) {
            displayBooks(this.allBooks);
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        List<BookBean> filtered = allBooks.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lowerQuery) ||
                        b.getAuthor().toLowerCase().contains(lowerQuery))
                .toList();

        displayBooks(filtered);
    }

    private void openEditPopup(BookBean book) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Gestione Libro: " + book.getTitle());
        dialog.setHeaderText("Modifica o Ritira dal mercato");

        VBox dialogPane = new VBox(15);
        dialogPane.setPadding(new Insets(20));

        Label coverLbl = new Label("URL Copertina:");
        coverLbl.setStyle("-fx-font-family: 'Georgia'; -fx-font-weight: bold; -fx-text-fill: #3A352F;");
        TextField coverField = new TextField(book.getImageUrl());
        coverField.setStyle("-fx-background-color: white; -fx-border-color: #D3C5B1; -fx-border-radius: 8; -fx-padding: 8;");

        Label descLbl = new Label("Trama:");
        descLbl.setStyle("-fx-font-family: 'Georgia'; -fx-font-weight: bold; -fx-text-fill: #3A352F;");
        TextArea descArea = new TextArea(book.getDescription());
        descArea.setWrapText(true);
        descArea.setPrefRowCount(4);
        descArea.setStyle("-fx-control-inner-background: white; -fx-border-color: #D3C5B1; -fx-border-radius: 8;");

        dialogPane.getChildren().addAll(coverLbl, coverField, descLbl, descArea);
        dialog.getDialogPane().setContent(dialogPane);

        ButtonType saveType = new ButtonType("Salva Modifiche", ButtonBar.ButtonData.OK_DONE);
        ButtonType deleteType = new ButtonType("Elimina Libro", ButtonBar.ButtonData.LEFT);
        ButtonType cancelType = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(deleteType, saveType, cancelType);

        javafx.scene.Node deleteBtn = dialog.getDialogPane().lookupButton(deleteType);
        deleteBtn.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        dialog.showAndWait().ifPresent(scelta -> {
            try {
                if (scelta == saveType) {
                    book.setImageUrl(coverField.getText().trim());
                    book.setDescription(descArea.getText().trim());
                    appController.updateExistingBook(book);
                    loadPublisherCatalog();

                } else if (scelta == deleteType) {
                    appController.removeBookFromCatalog(book.getTitle());
                    loadPublisherCatalog();
                }
            } catch (Exception e) {
                AppLogger.logError("Errore durante l'operazione sul libro: " + e.getMessage());
            }
        });
    }
}