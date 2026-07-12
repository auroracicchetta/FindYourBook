package it.ispwproject.findyourbook.view.gui;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class SearchResultsGUIView extends DashboardGUIView {

    public VBox buildRoot(String username, List<BookBean> books, String lastQuery,
                          Runnable onBack, Consumer<String> onSearch,
                          Runnable onLogout, Runnable onMyBooksClick,
                          Consumer<BookBean> onBookClick,
                          BiConsumer<BookBean, ReadingStatus> onStatusChange,
                          BiConsumer<BookBean, Integer> onRate) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20, 50, 20, 50));
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        HBox navbar = super.buildNavbar(username, onMyBooksClick, onLogout, onSearch);

         // --- MAGIA: Scriviamo la ricerca precedente nella barra! ---
        if (lastQuery != null) {
            TextField searchBar = (TextField) navbar.getChildren().get(3);
            searchBar.setText(lastQuery);
        }
        // 2. Togliamo l'evidenziatura dalla "Home" e la rendiamo cliccabile per tornare indietro!
        Label homeLabel = (Label) navbar.getChildren().get(1);
        homeLabel.getStyleClass().clear();
        homeLabel.getStyleClass().add("nav-link");
        homeLabel.setStyle("-fx-cursor: hand;");
        homeLabel.setOnMouseClicked(e -> onBack.run());

        Button backBtn = new Button("< Indietro");
        backBtn.getStyleClass().add("back-link-button");
        backBtn.setOnAction(e -> onBack.run());

        TilePane grid = new TilePane();
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setPrefColumns(2);

        for (BookBean book : books) {
            VBox card = super.buildBookCard(
                    null,
                    book,
                    book.getStatus() != null ? book.getStatus().name() : null,
                    newStatusStr -> {
                        ReadingStatus statusEnum = null;

                        // Controllo robusto: gestisce sia i display name che i name() dell'Enum
                        if (newStatusStr != null && !newStatusStr.equals("Rimuovi libro") && !newStatusStr.equals("RIMUOVI")) {
                            if (newStatusStr.equals(ReadingStatus.TO_READ.getDisplayName()) || newStatusStr.equals(ReadingStatus.TO_READ.name())) {
                                statusEnum = ReadingStatus.TO_READ;
                            } else if (newStatusStr.equals(ReadingStatus.READING.getDisplayName()) || newStatusStr.equals(ReadingStatus.READING.name())) {
                                statusEnum = ReadingStatus.READING;
                            } else if (newStatusStr.equals(ReadingStatus.READ.getDisplayName()) || newStatusStr.equals(ReadingStatus.READ.name())) {
                                statusEnum = ReadingStatus.READ;
                            } else {
                                // Fallback di sicurezza estrema
                                try {
                                    statusEnum = ReadingStatus.valueOf(newStatusStr);
                                } catch (IllegalArgumentException ignored) {}
                            }
                        }

                        onStatusChange.accept(book, statusEnum);
                        book.setStatus(statusEnum);
                    },
                    rating -> {
                        onRate.accept(book, rating);
                        book.setRating(rating);
                    },
                    () -> onBookClick.accept(book)
            );
            grid.getChildren().add(card);
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        root.getChildren().addAll(navbar, backBtn, scroll);
        return root;
    }

}