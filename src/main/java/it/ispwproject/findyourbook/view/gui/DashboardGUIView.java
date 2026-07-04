package it.ispwproject.findyourbook.view.gui;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import javafx.geometry.Insets;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public abstract class DashboardGUIView {

    public static final String BG_COLOR = "#EBE2D4";
    public static final String TEXT_DARK = "#4A3F35";
    public static final String BTN_GREEN = "#85A38D";

    protected static final String LBL_DA_LEGGERE = "Da leggere";
    protected static final String LBL_IN_LETTURA = "In lettura";
    protected static final String LBL_LETTO = "Letto";

    protected static final String DB_DA_LEGGERE = "DA_LEGGERE";
    protected static final String DB_IN_LETTURA = "IN_LETTURA";
    protected static final String DB_LETTO = "LETTO";

    public HBox buildNavbar(Runnable onMyBooksClick, Runnable onLogout, Consumer<String> onSearch) {
        HBox navbar = new HBox(40);
        navbar.setAlignment(Pos.CENTER_LEFT);

        Label brand = new Label("FindYourBook");
        brand.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        brand.setMinWidth(Region.USE_PREF_SIZE);

        Label homeLink = new Label("Home");
        homeLink.getStyleClass().add("nav-link-active");

        Label myBooksLink = new Label("I miei libri");
        myBooksLink.getStyleClass().add("nav-link");
        if (onMyBooksClick != null) {
            myBooksLink.setOnMouseClicked(e -> onMyBooksClick.run());
        }

        TextField searchBar = new TextField();
        searchBar.setPromptText("Cerca libri...");
        searchBar.setPrefWidth(350);
        searchBar.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 20; -fx-padding: 10 20;");
        if (onSearch != null) {
            searchBar.setOnAction(e -> {
                String query = searchBar.getText().trim();
                if (!query.isEmpty()) onSearch.accept(query);
            });
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- AVATAR DINAMICO E MENU EDITORIALE ---
        String tempUser = "U";
        try {
            var user = it.ispwproject.findyourbook.pattern.singleton.SessionManager.getInstance().getLoggedUser();
            if (user != null) tempUser = user.getUsername();
        } catch (Exception ignored) {}

        // Creiamo una variabile final per poterla usare dentro la lambda
        final String username = tempUser;
        String initial = username.substring(0, 1).toUpperCase();

        MenuButton profileMenu = new MenuButton(initial);
        // Stile bottone nocciola
        profileMenu.setStyle("-fx-background-color: #A67B5B; -fx-text-fill: white; -fx-font-family: 'Arial'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 50; -fx-cursor: hand;");
        profileMenu.setPrefSize(45, 45);

        MenuItem profileInfo = new MenuItem("I miei dati");
        MenuItem logoutItem = new MenuItem("Logout");

        // Stile "Editoriale": Sfondo Panna e testo Marrone Scuro
        String menuStyle = "-fx-background-color: #FDFBF7; -fx-text-fill: " + TEXT_DARK + "; -fx-font-family: 'Georgia'; -fx-font-size: 14px;";
        profileInfo.setStyle(menuStyle);
        logoutItem.setStyle(menuStyle);

        profileInfo.setOnAction(e -> {
            new it.ispwproject.findyourbook.controller.gui.UserProfileGUI().show();
        });

        logoutItem.setOnAction(e -> onLogout.run());
        profileMenu.getItems().addAll(profileInfo, logoutItem);

        navbar.getChildren().addAll(brand, homeLink, myBooksLink, searchBar, spacer, profileMenu);
        return navbar;
    }

    public VBox buildBookCard(String sectionTitle, BookBean book, String currentStatus, Consumer<String> onStatusChange, IntConsumer onRate, Runnable onClick) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 3);");
        card.setPrefWidth(320);
        card.setMaxWidth(320);

        if (sectionTitle != null && !sectionTitle.trim().isEmpty()) {
            Label header = new Label(sectionTitle);
            header.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
            card.getChildren().add(header);
        }

        card.setOnMouseClicked(e -> {
            if (onClick != null) onClick.run();
        });

        HBox content = new HBox(15);
        content.setAlignment(Pos.CENTER_LEFT);

        ImageView coverView = createCoverView(book.getImageUrl());
        VBox infoBox = createInfoBox(book, currentStatus, onStatusChange, onRate);

        content.getChildren().addAll(coverView, infoBox);
        card.getChildren().add(content);

        return card;
    }

    private ImageView createCoverView(String imageUrl) {
        ImageView coverView = new ImageView();
        coverView.setFitWidth(90);
        coverView.setFitHeight(140);
        coverView.setPreserveRatio(true);

        if (imageUrl != null && imageUrl.startsWith("http")) {
            Image image = new Image(imageUrl, 90, 140, true, true, true);
            coverView.setImage(image);
        } else {
            coverView.setStyle("-fx-background-color: #D3C5B1;");
        }
        return coverView;
    }

    private VBox createInfoBox(BookBean book, String currentStatus, Consumer<String> onStatusChange, IntConsumer onRate) {
        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label titleL = new Label(book.getTitle());
        titleL.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        titleL.setWrapText(true);
        titleL.setMaxWidth(180);

        Label authorL = new Label("di " + book.getAuthor());
        authorL.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 13px; -fx-text-fill: #7A7A7A; -fx-font-style: italic;");

        MenuButton readBtn = createReadMenu(currentStatus, onStatusChange, book.getTitle());
        HBox ratingBox = createRatingBox(book.getRating(), onRate);

        infoBox.getChildren().addAll(titleL, authorL, readBtn, ratingBox);
        return infoBox;
    }

    private MenuButton createReadMenu(String currentStatus, Consumer<String> onStatusChange, String bookTitle) {
        String btnText = "Aggiungi a...";

        if (DB_LETTO.equals(currentStatus)) {
            btnText = LBL_LETTO;
        } else if (DB_IN_LETTURA.equals(currentStatus)) {
            btnText = LBL_IN_LETTURA;
        } else if (DB_DA_LEGGERE.equals(currentStatus)) {
            btnText = LBL_DA_LEGGERE;
        }

        MenuButton readBtn = new MenuButton(btnText);
        readBtn.setOnMouseClicked(e -> e.consume());
        readBtn.setStyle("-fx-background-color: " + BTN_GREEN + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");

        MenuItem optWantToRead = new MenuItem(LBL_DA_LEGGERE);
        MenuItem optReading = new MenuItem(LBL_IN_LETTURA);
        MenuItem optRead = new MenuItem(LBL_LETTO);

        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem optRemove = new MenuItem("Rimuovi libro");
        optRemove.setStyle("-fx-text-fill: #C0392B;");

        readBtn.getItems().addAll(optWantToRead, optReading, optRead, separator, optRemove);

        optWantToRead.setOnAction(e -> { readBtn.setText(LBL_DA_LEGGERE); if(onStatusChange != null) onStatusChange.accept(DB_DA_LEGGERE); });
        optReading.setOnAction(e -> { readBtn.setText(LBL_IN_LETTURA); if(onStatusChange != null) onStatusChange.accept(DB_IN_LETTURA); });
        optRead.setOnAction(e -> { readBtn.setText(LBL_LETTO); if(onStatusChange != null) onStatusChange.accept(DB_LETTO); });

        optRemove.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Rimuovi Libro");
            alert.setHeaderText("Vuoi rimuovere '" + bookTitle + "' dalla tua libreria?");
            alert.setContentText("Questa azione rimuoverà permanentemente il libro, incluse le tue valutazioni.");

            ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType btnOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(btnAnnulla, btnOk);

            alert.showAndWait().ifPresent(type -> {
                if (type == btnOk && onStatusChange != null) {
                    onStatusChange.accept("RIMUOVI");
                }
            });
        });

        return readBtn;
    }

    private HBox createRatingBox(int initialRating, IntConsumer onRate) {
        HBox ratingBox = new HBox(2);
        ratingBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(ratingBox, new Insets(10, 0, 0, 0));

        Label valutaText = new Label("Valuta: ");
        valutaText.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 13px; -fx-text-fill: " + TEXT_DARK + ";");
        ratingBox.getChildren().add(valutaText);

        final int[] clickedRating = {initialRating};
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
        star.setStyle("-fx-font-size: 18px; -fx-text-fill: #E6B800; -fx-cursor: hand;");

        star.setOnMouseEntered(e -> updateStars(stars, starValue));
        star.setOnMouseExited(e -> updateStars(stars, clickedRating[0]));

        star.setOnMouseClicked(e -> {
            e.consume();
            clickedRating[0] = starValue;
            updateStars(stars, starValue);
            AppLogger.logInfo("Votato " + starValue + " stelle!");
            if (onRate != null) onRate.accept(starValue);
        });
        return star;
    }

    private void updateStars(Label[] stars, int rating) {
        for (int i = 0; i < 5; i++) {
            stars[i].setText(i < rating ? "★" : "☆");
        }
    }

    protected VBox buildGenreTile(String filename, String title, Consumer<String> onClick) {
        VBox tile = new VBox(10);
        tile.setAlignment(Pos.CENTER);
        tile.setStyle("-fx-cursor: hand;");

        ImageView icon = new ImageView();
        try {
            String imagePath = "/icons/" + filename;
            InputStream is = getClass().getResourceAsStream(imagePath);

            if (is != null) {
                Image img = new Image(is);
                icon.setImage(img);
            } else {
                AppLogger.logError("Immagine non trovata: " + imagePath);
            }
        } catch (Exception e) {
            AppLogger.logError("Errore caricamento immagine: " + filename);
        }

        icon.setFitWidth(50);
        icon.setFitHeight(50);

        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 14px; -fx-text-fill: #3e3831; -fx-font-weight: bold;");

        tile.setOnMouseClicked(e -> {
            if (onClick != null) {
                onClick.accept(title.toLowerCase());
            }
        });

        tile.setOnMouseEntered(e -> icon.setOpacity(0.7));
        tile.setOnMouseExited(e -> icon.setOpacity(1.0));

        tile.getChildren().addAll(icon, lbl);
        return tile;
    }
}