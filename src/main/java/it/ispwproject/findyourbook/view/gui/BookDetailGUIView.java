package it.ispwproject.findyourbook.view.gui;

import it.ispwproject.findyourbook.bean.BookBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

public class BookDetailGUIView extends DashboardGUIView {

    // --- COSTANTI PER RISOLVERE IL CODE SMELL DELLE STRINGHE DUPLICATE ---
    private static final String LBL_DA_LEGGERE = "Da leggere";
    private static final String LBL_IN_LETTURA = "In lettura";
    private static final String LBL_LETTO = "Letto";

    private static final String DB_DA_LEGGERE = "DA_LEGGERE";
    private static final String DB_IN_LETTURA = "IN_LETTURA";
    private static final String DB_LETTO = "LETTO";

    public VBox buildRoot(BookBean book, String currentStatus, Runnable onBack, Runnable onHomeClick, Runnable onMyBooksClick, Runnable onLogout, java.util.function.Consumer<String> onSearch) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20, 50, 20, 50));
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // 1. Navbar superiore
        HBox navbar = super.buildNavbar(onMyBooksClick, onLogout, onSearch);
        Label homeLabel = (Label) navbar.getChildren().get(1);
        homeLabel.getStyleClass().clear();
        homeLabel.getStyleClass().add("nav-link");
        homeLabel.setStyle("-fx-cursor: hand;");
        homeLabel.setOnMouseClicked(e -> onHomeClick.run());

        // 2. Bottone Indietro
        Button backBtn = createBackButton(onBack);

        // 3. Contenitore Principale
        HBox mainContent = new HBox(40);
        mainContent.setAlignment(Pos.TOP_LEFT);
        mainContent.setPadding(new Insets(20, 0, 0, 0));

        // Risolto code smell di Complessità Cognitiva estraendo la costruzione delle colonne in metodi separati
        VBox leftColumn = createLeftColumn(book, currentStatus);
        VBox rightColumn = createRightColumn(book);

        mainContent.getChildren().addAll(leftColumn, rightColumn);
        root.getChildren().addAll(navbar, backBtn, mainContent);

        return root;
    }

    // --- METODI ESTRATTI PER ABBATTERE LA COMPLESSITÀ COGNITIVA ---

    private Button createBackButton(Runnable onBack) {
        Button backBtn = new Button("< Indietro");
        String stileBase = "-fx-background-color: transparent; -fx-padding: 8 15; -fx-cursor: hand; -fx-text-fill: #4A3F35; -fx-font-weight: bold; -fx-font-size: 14px;";
        String stileHover = stileBase + " -fx-underline: true;";

        backBtn.setStyle(stileBase);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(stileHover));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(stileBase));
        backBtn.setOnAction(e -> onBack.run());

        return backBtn;
    }

    private VBox createLeftColumn(BookBean book, String currentStatus) {
        VBox leftColumn = new VBox(15);
        leftColumn.setAlignment(Pos.TOP_CENTER);
        leftColumn.setPrefWidth(200);

        ImageView coverView = new ImageView();
        coverView.setFitWidth(180);
        coverView.setFitHeight(270);
        coverView.setPreserveRatio(true);
        if (book.getImageUrl() != null && book.getImageUrl().startsWith("http")) {
            coverView.setImage(new Image(book.getImageUrl(), 180, 270, true, true, true));
        }

        MenuButton statusBtn = createStatusButton(book, currentStatus);
        HBox ratingBox = createRatingBox(book);

        VBox actionBox = new VBox(20, statusBtn, ratingBox);
        actionBox.setAlignment(Pos.CENTER);
        VBox.setMargin(actionBox, new Insets(20, 0, 0, 0));

        leftColumn.getChildren().addAll(coverView, actionBox);
        return leftColumn;
    }

    private MenuButton createStatusButton(BookBean book, String currentStatus) {
        String statusText = "Aggiungi a...";
        if (DB_DA_LEGGERE.equals(currentStatus)) statusText = LBL_DA_LEGGERE;
        else if (DB_IN_LETTURA.equals(currentStatus)) statusText = LBL_IN_LETTURA;
        else if (DB_LETTO.equals(currentStatus)) statusText = LBL_LETTO;

        MenuButton statusBtn = new MenuButton(statusText);
        statusBtn.setStyle("-fx-background-color: #85A38D; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 5 15;");

        MenuItem optWantToRead = new MenuItem(LBL_DA_LEGGERE);
        MenuItem optReading = new MenuItem(LBL_IN_LETTURA);
        MenuItem optRead = new MenuItem(LBL_LETTO);

        optWantToRead.setOnAction(e -> {
            statusBtn.setText(LBL_DA_LEGGERE);
            new it.ispwproject.findyourbook.controller.applicativo.UserLibraryController().saveBookToLibrary(book, DB_DA_LEGGERE);
        });
        optReading.setOnAction(e -> {
            statusBtn.setText(LBL_IN_LETTURA);
            new it.ispwproject.findyourbook.controller.applicativo.UserLibraryController().saveBookToLibrary(book, DB_IN_LETTURA);
        });
        optRead.setOnAction(e -> {
            statusBtn.setText(LBL_LETTO);
            new it.ispwproject.findyourbook.controller.applicativo.UserLibraryController().saveBookToLibrary(book, DB_LETTO);
        });

        statusBtn.getItems().addAll(optWantToRead, optReading, optRead);
        return statusBtn;
    }

    private HBox createRatingBox(BookBean book) {
        HBox ratingBox = new HBox(5);
        ratingBox.setAlignment(Pos.CENTER);

        Label valutaTesto = new Label("Valuta: ");
        valutaTesto.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 14px; -fx-text-fill: " + TEXT_DARK + ";");
        ratingBox.getChildren().add(valutaTesto);

        final int[] clickedRating = {book.getRating()};
        Label[] stars = new Label[5];

        for (int i = 0; i < 5; i++) {
            stars[i] = createStar(i, clickedRating, stars, book);
            ratingBox.getChildren().add(stars[i]);
        }
        return ratingBox;
    }

    private Label createStar(int index, int[] clickedRating, Label[] stars, BookBean book) {
        int starValue = index + 1;
        Label star = new Label(starValue <= clickedRating[0] ? "★" : "☆");
        star.setStyle("-fx-font-size: 22px; -fx-text-fill: #E6B800; -fx-cursor: hand;");

        star.setOnMouseEntered(e -> updateStars(stars, starValue));
        star.setOnMouseExited(e -> updateStars(stars, clickedRating[0]));
        star.setOnMouseClicked(e -> {
            clickedRating[0] = starValue;
            updateStars(stars, starValue);

            new it.ispwproject.findyourbook.controller.applicativo.UserLibraryController().rateBook(book, starValue);
            book.setRating(starValue);
        });
        return star;
    }

    private void updateStars(Label[] stars, int rating) {
        for (int i = 0; i < 5; i++) {
            stars[i].setText(i < rating ? "★" : "☆");
        }
    }

    private VBox createRightColumn(BookBean book) {
        VBox rightColumn = new VBox(15);
        rightColumn.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label(book.getTitle());
        titleLabel.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        titleLabel.setWrapText(true);

        Label authorLabel = new Label("di " + book.getAuthor());
        authorLabel.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 18px; -fx-text-fill: #7A7A7A; -fx-font-style: italic;");

        Label descriptionLabel = new Label(book.getDescription());
        descriptionLabel.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 14px; -fx-text-fill: " + TEXT_DARK + "; -fx-line-spacing: 5px;");
        descriptionLabel.setWrapText(true);

        javafx.scene.control.ScrollPane scrollDesc = new javafx.scene.control.ScrollPane(descriptionLabel);
        scrollDesc.setFitToWidth(true);
        scrollDesc.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollDesc.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollDesc.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
        scrollDesc.setPrefHeight(350);
        scrollDesc.getStyleClass().add("transparent-pane");

        VBox descriptionCard = new VBox(scrollDesc);
        descriptionCard.setStyle("-fx-background-color: #FDFBF7; -fx-background-radius: 20; -fx-padding: 25; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");
        descriptionCard.setMaxWidth(600);
        VBox.setMargin(descriptionCard, new Insets(15, 0, 0, 0));

        rightColumn.getChildren().addAll(titleLabel, authorLabel, descriptionCard);

        return rightColumn;
    }
}