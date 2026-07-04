package it.ispwproject.findyourbook.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class UserLibraryGUIView extends DashboardGUIView {

    private FlowPane booksGrid;

    // --- FIRMA MODIFICATA: Aggiunti 'username' e 'readBooksCount' ---
    public VBox buildRoot(String username, int readBooksCount, Runnable onHomeClick, Runnable onLogout, Consumer<String> onSearch, Consumer<String> onFilterClick) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(20, 50, 40, 50));
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        HBox navbar = super.buildNavbar(null, onLogout, onSearch);

        Label homeLabel = (Label) navbar.getChildren().get(1);
        homeLabel.getStyleClass().clear();
        homeLabel.getStyleClass().add("nav-link");
        homeLabel.setOnMouseClicked(e -> onHomeClick.run());

        Label myBooksLabel = (Label) navbar.getChildren().get(2);
        myBooksLabel.getStyleClass().clear();
        myBooksLabel.getStyleClass().add("nav-link-active");

        Region sep = new Region();
        sep.setMinHeight(2);
        sep.setStyle("-fx-background-color: #FFFFFF;");

        VBox headerBox = new VBox(35);
        headerBox.setAlignment(Pos.CENTER);

        HBox profileBox = new HBox(40);
        profileBox.setAlignment(Pos.CENTER);

        // --- AVATAR NOCCIOLA GIGANTE ---
        String initial = (username != null && !username.isEmpty()) ? username.substring(0, 1).toUpperCase() : "U";
        Label avatarIcon = new Label(initial);
        avatarIcon.setMinSize(100, 100);
        avatarIcon.setMaxSize(100, 100);
        avatarIcon.setAlignment(Pos.CENTER);
        avatarIcon.setStyle("-fx-background-color: #A67B5B; -fx-text-fill: white; -fx-font-family: 'Arial'; -fx-font-size: 45px; -fx-font-weight: bold; -fx-background-radius: 100;");

        VBox profileInfo = new VBox(15);
        profileInfo.setAlignment(Pos.CENTER_LEFT);

        // --- BENVENUTO PERSONALIZZATO ---
        Label nameLabel = new Label("Bentornata, " + username + "!");
        nameLabel.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER_LEFT);

        // --- CONTEGGIO DINAMICO DEI LIBRI ---
        Label stat1 = new Label(readBooksCount + "\nLibri letti");
        stat1.setStyle("-fx-font-family: 'Georgia'; -fx-text-alignment: center; -fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + TEXT_DARK + ";");

        Label sep1 = new Label("|");
        sep1.setStyle("-fx-font-size: 26px; -fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: lighter;");

        Label stat2 = new Label("Preferiti");
        stat2.setStyle("-fx-font-family: 'Georgia'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: " + TEXT_DARK + ";");

        Label sep2 = new Label("|");
        sep2.setStyle("-fx-font-size: 26px; -fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: lighter;");

        Label stat3 = new Label("Generi");
        stat3.setStyle("-fx-font-family: 'Georgia'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: " + TEXT_DARK + ";");

        statsBox.getChildren().addAll(stat1, sep1, stat2, sep2, stat3);
        profileInfo.getChildren().addAll(nameLabel, statsBox);

        profileBox.getChildren().addAll(avatarIcon, profileInfo);

        HBox filterBox = new HBox(20);
        filterBox.setAlignment(Pos.CENTER);

        Button btnToRead = createFilterButton("Da leggere", () -> onFilterClick.accept("DA_LEGGERE"));
        Button btnReading = createFilterButton("In lettura", () -> onFilterClick.accept("IN_LETTURA"));
        Button btnRead = createFilterButton("Letti", () -> onFilterClick.accept("LETTO"));

        filterBox.getChildren().addAll(btnToRead, btnReading, btnRead);

        headerBox.getChildren().addAll(profileBox, filterBox);

        booksGrid = new FlowPane();
        booksGrid.setHgap(30);
        booksGrid.setVgap(30);
        booksGrid.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(booksGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BG_COLOR + ";");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("transparent-pane");

        root.getChildren().addAll(navbar, sep, headerBox, scrollPane);

        return root;
    }

    private Button createFilterButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 16px; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 20; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        btn.setOnAction(e -> action.run());

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + TEXT_DARK + "; -fx-text-fill: #FFFFFF; -fx-font-size: 16px; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 20; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 16px; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 20; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"));
        return btn;
    }

    public void populateGrid(java.util.List<javafx.scene.layout.VBox> bookCards) {
        booksGrid.getChildren().clear();
        if (bookCards.isEmpty()) {
            Label emptyLabel = new Label("Nessun libro trovato in questa sezione.");
            emptyLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #7A7A7A; -fx-font-style: italic;");
            booksGrid.getChildren().add(emptyLabel);
        } else {
            booksGrid.getChildren().addAll(bookCards);
        }
    }
}