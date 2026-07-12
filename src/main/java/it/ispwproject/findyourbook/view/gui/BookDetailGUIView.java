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

    public VBox buildRoot(String username, BookBean book, ReadingStatus currentStatus,
                          Consumer<String> onStatusChange, IntConsumer onRate,
                          Runnable onBack, Runnable onHomeClick,
                          Runnable onMyBooksClick, Runnable onLogout, Consumer<String> onSearch) {
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

        // 2. Bottone Indietro
        Button backBtn = createBackButton(onBack);

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

    private Button createBackButton(Runnable onBack) {
        Button backBtn = new Button("< Indietro");
        backBtn.getStyleClass().add("back-link-button");
        backBtn.setOnAction(e -> onBack.run());
        return backBtn;
    }

    private VBox createLeftColumn(BookBean book, ReadingStatus currentStatus, Consumer<String> onStatusChange, IntConsumer onRate) {
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
        MenuButton statusBtn = createStatusButton(book, currentStatus, onStatusChange, ratingBox, onRate);

        VBox actionBox = new VBox(20, statusBtn, ratingBox);
        actionBox.setAlignment(Pos.CENTER);
        VBox.setMargin(actionBox, new Insets(20, 0, 0, 0));

        leftColumn.getChildren().addAll(coverView, actionBox);
        return leftColumn;
    }

    private MenuButton createStatusButton(BookBean book, ReadingStatus currentStatus, Consumer<String> onStatusChange, HBox ratingBox, IntConsumer onRate) {
        String statusText = "Aggiungi a...";
        if (currentStatus == ReadingStatus.TO_READ) statusText = ReadingStatus.TO_READ.getDisplayName();
        else if (currentStatus == ReadingStatus.READING) statusText = ReadingStatus.READING.getDisplayName();
        else if (currentStatus == ReadingStatus.READ) statusText = ReadingStatus.READ.getDisplayName();

        MenuButton statusBtn = new MenuButton(statusText);
        statusBtn.setStyle("-fx-background-color: #85A38D; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand; -fx-padding: 5 15;");

        // Usiamo i nomi display dell'Enum per le opzioni
        MenuItem optWantToRead = new MenuItem(ReadingStatus.TO_READ.getDisplayName());
        MenuItem optReading = new MenuItem(ReadingStatus.READING.getDisplayName());
        MenuItem optRead = new MenuItem(ReadingStatus.READ.getDisplayName());

        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem optRemove = new MenuItem("Rimuovi libro");
        optRemove.setStyle("-fx-text-fill: #C0392B;");

        // Azioni: aggiornano il testo e passano al controller la stringa corretta
        optWantToRead.setOnAction(e -> {
            statusBtn.setText(ReadingStatus.TO_READ.getDisplayName());
            onStatusChange.accept(ReadingStatus.TO_READ.getDisplayName());
        });

        optReading.setOnAction(e -> {
            statusBtn.setText(ReadingStatus.READING.getDisplayName());
            onStatusChange.accept(ReadingStatus.READING.getDisplayName());
        });

        optRead.setOnAction(e -> {
            statusBtn.setText(ReadingStatus.READ.getDisplayName());
            onStatusChange.accept(ReadingStatus.READ.getDisplayName());
        });

        optRemove.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Rimuovi Libro");
            alert.setHeaderText("Vuoi rimuovere '" + book.getTitle() + "' dalla tua libreria?");
            alert.setContentText("Questa azione rimuoverà permanentemente il libro, incluse le tue valutazioni.");

            ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType btnOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(btnAnnulla, btnOk);

            alert.showAndWait().ifPresent(type -> {
                if (type == btnOk) {
                    statusBtn.setText("Aggiungi a...");
                    book.setStatus(null);
                    onStatusChange.accept("Rimuovi libro"); // La GUI principale si aspetta questa stringa!

                    for (int i = 1; i <= 5; i++) ((Label) ratingBox.getChildren().get(i)).setText("☆");
                    int[] clickedRating = (int[]) ratingBox.getProperties().get("clickedRating");
                    if (clickedRating != null) clickedRating[0] = 0;
                    if (onRate != null) onRate.accept(0);
                }
            });
        });

        statusBtn.getItems().addAll(optWantToRead, optReading, optRead, separator, optRemove);
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