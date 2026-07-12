package it.ispwproject.findyourbook.view.gui;

import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class UserLibraryGUIView extends DashboardGUIView {

    private FlowPane booksGrid;
    private Button btnToRead;
    private Button btnReading;
    private Button btnRead;

    private ReadingStatus currentActiveStatus = null;

    private final String COLOR_INACTIVE = "-fx-background-color: #FFFFFF; -fx-text-fill: #3A352F; -fx-background-radius: 40; -fx-cursor: hand; -fx-font-family: 'Georgia'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 12 40;";
    private final String COLOR_ACTIVE = "-fx-background-color: #3A352F; -fx-text-fill: #FFFFFF; -fx-background-radius: 40; -fx-cursor: hand; -fx-font-family: 'Georgia'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 12 40;";

    public VBox buildRoot(String username, int readBooksCount, Runnable onHomeClick, Runnable onLogout, Consumer<String> onSearch, Consumer<ReadingStatus> onFilterClick) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(20, 50, 40, 50));
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        HBox navbar = super.buildNavbar(username, null, onLogout, onSearch);

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

        // Contenitore principale dell'intestazione
        VBox headerBox = new VBox(35);
        headerBox.setAlignment(Pos.CENTER);

        // 1. ZONA PROFILO: Avatar e Nome
        HBox profileBox = new HBox(30);
        profileBox.setAlignment(Pos.CENTER);

        String initial = (username != null && !username.isEmpty()) ? username.substring(0, 1).toUpperCase() : "U";
        Label avatarIcon = new Label(initial);
        avatarIcon.setMinSize(100, 100);
        avatarIcon.setMaxSize(100, 100);
        avatarIcon.setAlignment(Pos.CENTER);
        avatarIcon.setStyle("-fx-background-color: #C1A68D; -fx-text-fill: white; -fx-font-family: 'Arial'; -fx-font-size: 45px; -fx-font-weight: bold; -fx-background-radius: 100;");

        Label nameLabel = new Label("La tua libreria, " + username + "!");
        nameLabel.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        profileBox.getChildren().addAll(avatarIcon, nameLabel);

        // 2. ZONA STATISTICHE: Solo "Libri letti", ingrandito e in corsivo
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);

        Label stat1 = new Label("Libri letti: " + readBooksCount);
        stat1.setStyle("-fx-font-family: 'Georgia'; -fx-font-weight: bold; -fx-font-style: italic; -fx-font-size: 24px; -fx-text-fill: " + TEXT_DARK + ";");

        statsBox.getChildren().add(stat1);

        // 3. ZONA FILTRI: Bottoni ancora più grandi e distanziati
        HBox filterBox = new HBox(40); // Spazio tra i bottoni aumentato
        filterBox.setAlignment(Pos.CENTER);

        btnToRead = new Button("Da leggere");
        btnReading = new Button("In lettura");
        btnRead = new Button("Letti");

        resetButtonColors();

        setupHover(btnToRead, ReadingStatus.TO_READ);
        setupHover(btnReading, ReadingStatus.READING);
        setupHover(btnRead, ReadingStatus.READ);

        // 2. Assegniamo gli eventi click: passano solo l'informazione al controller
        btnToRead.setOnAction(e -> onFilterClick.accept(ReadingStatus.TO_READ));
        btnReading.setOnAction(e -> onFilterClick.accept(ReadingStatus.READING));
        btnRead.setOnAction(e -> onFilterClick.accept(ReadingStatus.READ));

        filterBox.getChildren().addAll(btnToRead, btnReading, btnRead);

        // Assembliamo l'headerBox impilando le tre righe
        headerBox.getChildren().addAll(profileBox, statsBox, filterBox);

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

    private void setupHover(Button btn, ReadingStatus status) {
        btn.setOnMouseEntered(e -> {
            if (currentActiveStatus != status) {
                btn.setStyle(COLOR_ACTIVE);
            }
        });
        btn.setOnMouseExited(e -> {
            if (currentActiveStatus != status) {
                btn.setStyle(COLOR_INACTIVE);
            }
        });
    }

    private void resetButtonColors() {
        btnToRead.setStyle(COLOR_INACTIVE);
        btnReading.setStyle(COLOR_INACTIVE);
        btnRead.setStyle(COLOR_INACTIVE);
    }

    public void setActiveButton(ReadingStatus status) {

        this.currentActiveStatus = status;
        resetButtonColors();
        if (status == null) return;

        if (status == ReadingStatus.TO_READ) {
            btnToRead.setStyle(COLOR_ACTIVE);
        } else if (status == ReadingStatus.READING) {
            btnReading.setStyle(COLOR_ACTIVE);
        } else if (status == ReadingStatus.READ) {
            btnRead.setStyle(COLOR_ACTIVE);
        }
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