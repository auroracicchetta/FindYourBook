package it.ispwproject.findyourbook.view.gui;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;
import java.util.function.IntConsumer;


public class BookDetailGUIView extends DashboardGUIView {


    // Tutti gli 11 parametri sono usati davvero (nessuno morto/inutile): sono le
    // callback delle azioni disponibili su questa schermata (navbar, rating,
    // cambio stato, navigazione indietro) piu' i dati del libro da mostrare.
    @SuppressWarnings("java:S107")
    public VBox buildRoot(String username, BookBean book, ReadingStatus currentStatus,
                          Consumer<ReadingStatus> onStatusChange, IntConsumer onRate,
                          Runnable onBack, Runnable onHomeClick,
                          Runnable onMyBooksClick, Runnable onLogout, Consumer<String> onSearch,
                          String originLabel) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20, 50, 20, 50));
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        // 1. Navbar superiore
        HBox navbar = super.buildNavbar(username, onMyBooksClick, onLogout, onSearch);
        Label homeLabel = (Label) navbar.getChildren().get(1);
        homeLabel.getStyleClass().clear();
        homeLabel.getStyleClass().add("nav-link");
        homeLabel.setStyle("-fx-cursor: hand;");
        homeLabel.setOnMouseClicked(e -> onHomeClick.run());

        // 2. Bottone Indietro: mostra esplicitamente da dove si e' arrivati
        Button backBtn = createBackButton(onBack, originLabel);

        // 3. Contenitore Principale
        HBox mainContent = new HBox(40);
        mainContent.setAlignment(Pos.TOP_LEFT);
        mainContent.setPadding(new Insets(20, 0, 0, 0));

        // Costruzione delle colonne
        VBox leftColumn = createLeftColumn(book, currentStatus, onStatusChange, onRate);
        VBox rightColumn = createRightColumn(book);

        mainContent.getChildren().addAll(leftColumn, rightColumn);
        root.getChildren().addAll(navbar, backBtn, mainContent);

        return root;
    }

    private Button createBackButton(Runnable onBack, String originLabel) {
        String label = (originLabel == null || originLabel.isBlank()) ? "Indietro" : "Torna " + originLabel;
        Button backBtn = new Button("< " + label);
        backBtn.getStyleClass().add("back-link-button");
        backBtn.setOnAction(e -> onBack.run());
        return backBtn;
    }

    private VBox createLeftColumn(BookBean book, ReadingStatus currentStatus, Consumer<ReadingStatus> onStatusChange, IntConsumer onRate) {
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

        HBox ratingBox = createRatingBox(book, onRate);
        MenuButton statusBtn = createStatusButton(currentStatus, onStatusChange);

        VBox actionBox = new VBox(20, statusBtn, ratingBox);
        actionBox.setAlignment(Pos.CENTER);
        VBox.setMargin(actionBox, new Insets(20, 0, 0, 0));

        leftColumn.getChildren().addAll(coverView, actionBox);
        return leftColumn;
    }


    private MenuButton createStatusButton(ReadingStatus currentStatus, Consumer<ReadingStatus> onStatusChange) {
        String statusText = "Aggiungi a...";
        if (currentStatus == ReadingStatus.TO_READ) statusText = ReadingStatus.TO_READ.getDisplayName();
        else if (currentStatus == ReadingStatus.READING) statusText = ReadingStatus.READING.getDisplayName();
        else if (currentStatus == ReadingStatus.READ) statusText = ReadingStatus.READ.getDisplayName();

        MenuButton statusBtn = new MenuButton(statusText);
        final String statusBtnBaseStyle = "-fx-background-color: #85A38D; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 5 15;";
        final String statusBtnHoverStyle = "-fx-background-color: #9DB9A5; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 5 15;";
        statusBtn.setStyle(statusBtnBaseStyle);
        statusBtn.setOnMouseEntered(e -> statusBtn.setStyle(statusBtnHoverStyle));
        statusBtn.setOnMouseExited(e -> statusBtn.setStyle(statusBtnBaseStyle));

        MenuItem optWantToRead = new MenuItem(ReadingStatus.TO_READ.getDisplayName());
        MenuItem optReading = new MenuItem(ReadingStatus.READING.getDisplayName());
        MenuItem optRead = new MenuItem(ReadingStatus.READ.getDisplayName());

        optWantToRead.setOnAction(e -> {
            statusBtn.setText(ReadingStatus.TO_READ.getDisplayName());
            onStatusChange.accept(ReadingStatus.TO_READ);
        });

        optReading.setOnAction(e -> {
            statusBtn.setText(ReadingStatus.READING.getDisplayName());
            onStatusChange.accept(ReadingStatus.READING);
        });

        optRead.setOnAction(e -> {
            statusBtn.setText(ReadingStatus.READ.getDisplayName());
            onStatusChange.accept(ReadingStatus.READ);
        });

        statusBtn.getItems().addAll(optWantToRead, optReading, optRead);
        return statusBtn;
    }

    private HBox createRatingBox(BookBean book, IntConsumer onRate) {
        HBox ratingBox = new HBox(5);
        ratingBox.setAlignment(Pos.CENTER);

        Label valutaTesto = new Label("Valuta: ");
        valutaTesto.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 14px; -fx-text-fill: " + TEXT_DARK + ";");
        ratingBox.getChildren().add(valutaTesto);

        final int[] clickedRating = {book.getRating()};
        ratingBox.getProperties().put("clickedRating", clickedRating);

        Label[] stars = new Label[5];
        for (int i = 0; i < 5; i++) {
            stars[i] = createStar(i, clickedRating, stars, onRate);
            ratingBox.getChildren().add(stars[i]);
        }
        return ratingBox;
    }

    private Label createStar(int index, int[] clickedRating, Label[] stars, IntConsumer onRate) {
        int starValue = index + 1;
        Label star = new Label(starValue <= clickedRating[0] ? "★" : "☆");
        star.setStyle("-fx-font-size: 22px; -fx-text-fill: #E6B800; -fx-cursor: hand;");

        star.setOnMouseEntered(e -> updateStars(stars, starValue));
        star.setOnMouseExited(e -> updateStars(stars, clickedRating[0]));
        star.setOnMouseClicked(e -> {
            clickedRating[0] = starValue;
            updateStars(stars, starValue);

            onRate.accept(starValue);
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