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
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public abstract class DashboardGUIView {

    public static final String BG_COLOR = "#EFE8D8"; // Beige ufficiale
    public static final String TEXT_DARK = "#3A352F"; // Marrone scuro ufficiale
    public static final String BTN_GREEN = "#8AAB8F"; // Verde salvia ufficiale

    public HBox buildNavbar(String username, Runnable onMyBooksClick, Runnable onLogout, Consumer<String> onSearch) {
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

        Button profileMenu = createAvatar(username, onLogout);

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

        HBox ratingBox = createRatingBox(book.getRating(), onRate);
        MenuButton readBtn = createReadMenu(currentStatus, onStatusChange, book.getTitle(), ratingBox, onRate);

        infoBox.getChildren().addAll(titleL, authorL, readBtn, ratingBox);
        return infoBox;
    }

    private MenuButton createReadMenu(String currentStatus, Consumer<String> onStatusChange, String bookTitle, HBox ratingBox, IntConsumer onRate) {
        String btnText = "Aggiungi a...";

        if (ReadingStatus.READ.name().equals(currentStatus)) {
            btnText = ReadingStatus.READ.getDisplayName();
        } else if (ReadingStatus.READING.name().equals(currentStatus)) {
            btnText = ReadingStatus.READING.getDisplayName();
        } else if (ReadingStatus.TO_READ.name().equals(currentStatus)) {
            btnText = ReadingStatus.TO_READ.getDisplayName();
        }

        MenuButton readBtn = new MenuButton(btnText);
        readBtn.setOnMouseClicked(e -> e.consume());
        readBtn.setStyle("-fx-background-color: " + BTN_GREEN + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");

// Usiamo i nomi grafici dell'Enum per le tendine
        MenuItem optWantToRead = new MenuItem(ReadingStatus.TO_READ.getDisplayName());
        MenuItem optReading = new MenuItem(ReadingStatus.READING.getDisplayName());
        MenuItem optRead = new MenuItem(ReadingStatus.READ.getDisplayName());

        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem optRemove = new MenuItem("Rimuovi libro");
        optRemove.setStyle("-fx-text-fill: #C0392B;");

        readBtn.getItems().addAll(optWantToRead, optReading, optRead, separator, optRemove);

        optWantToRead.setOnAction(e -> {
            readBtn.setText(ReadingStatus.TO_READ.getDisplayName());
            if (onStatusChange != null) onStatusChange.accept(ReadingStatus.TO_READ.name());
        });

        optReading.setOnAction(e -> {
            readBtn.setText(ReadingStatus.READING.getDisplayName());
            if (onStatusChange != null) onStatusChange.accept(ReadingStatus.READING.name());
        });

        optRead.setOnAction(e -> {
            readBtn.setText(ReadingStatus.READ.getDisplayName());
            if (onStatusChange != null) onStatusChange.accept(ReadingStatus.READ.name());
        });

        optRemove.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Rimuovi Libro");
            alert.setHeaderText("Vuoi rimuovere '" + bookTitle + "' dalla tua libreria?");
            alert.setContentText("Questa azione rimuoverà permanentemente il libro, incluse le tue valutazioni.");

            ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType btnOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(btnAnnulla, btnOk);

            alert.showAndWait().ifPresent(type -> {
                if (type == btnOk) {
                    if (onStatusChange != null) onStatusChange.accept("RIMUOVI");
                    readBtn.setText("Aggiungi a...");

                    for (int i = 1; i <= 5; i++) ((Label) ratingBox.getChildren().get(i)).setText("☆");
                    int[] clickedRating = (int[]) ratingBox.getProperties().get("clickedRating");
                    if (clickedRating != null) clickedRating[0] = 0;
                    if (onRate != null) onRate.accept(0);
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

    protected MenuButton buildProfileMenu(String username, Runnable onLogout) {
        String initial = (username != null && !username.isEmpty())
                ? username.substring(0, 1).toUpperCase()
                : "U";

        MenuButton profileMenu = new MenuButton(initial);

// STESSO STILE PER TUTTI: nocciola chiaro con testo bianco
        profileMenu.setStyle(
                "-fx-background-color: #C1A68D; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-family: 'Georgia'; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 50; " +
                        "-fx-cursor: hand;"
        );
        profileMenu.setPrefSize(45, 45);

// Voci del menu (uguali per tutti)
        MenuItem profileInfo = new MenuItem("I miei dati");
        MenuItem logoutItem = new MenuItem("Logout");

        String menuStyle =
                "-fx-background-color: #FDFBF7; " +
                        "-fx-text-fill: " + TEXT_DARK + "; " +
                        "-fx-font-family: 'Georgia'; " +
                        "-fx-font-size: 14px;";
        profileInfo.setStyle(menuStyle);
        logoutItem.setStyle(menuStyle);

        profileInfo.setOnAction(e -> {
            new it.ispwproject.findyourbook.controller.gui.UserProfileGUI().show();
        });
        logoutItem.setOnAction(e -> onLogout.run());

        profileMenu.getItems().addAll(profileInfo, logoutItem);
        return profileMenu;
    }

    /**
     * Metodo STATICO per creare l'avatar - garantito che funzioni sempre!
     * Usato come fallback se buildProfileMenu non funziona.
     */
    public static Button createAvatar(String username, Runnable onLogout) {
        String initial = (username != null && !username.isEmpty())
                ? username.substring(0, 1).toUpperCase()
                : "U";

        Button profileBtn = new Button(initial);

        String AVATAR_BASE = "-fx-background-color: #C1A68D; -fx-text-fill: white; -fx-font-family: 'Georgia'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 50; -fx-alignment: center; -fx-padding: 0; -fx-cursor: hand;";
        String AVATAR_HOVER = "-fx-background-color: #9F856D; -fx-text-fill: white; -fx-font-family: 'Georgia'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 50; -fx-alignment: center; -fx-padding: 0; -fx-cursor: hand;";

        profileBtn.setStyle(AVATAR_BASE);

        profileBtn.setOnMouseEntered(e -> profileBtn.setStyle(AVATAR_HOVER));
        profileBtn.setOnMouseExited(e -> profileBtn.setStyle(AVATAR_BASE));

        profileBtn.setMinSize(45, 45);
        profileBtn.setPrefSize(45, 45);
        profileBtn.setMaxSize(45, 45);

        // Creiamo il menu a tendina separato (ContextMenu)
        ContextMenu contextMenu = new ContextMenu();

        MenuItem profileInfo = new MenuItem("I miei dati");
        MenuItem logoutItem = new MenuItem("Logout");

        String menuStyle =
                "-fx-background-color: #FDFBF7; " +
                        "-fx-text-fill: " + TEXT_DARK + "; " +
                        "-fx-font-family: 'Georgia'; " +
                        "-fx-font-size: 14px;";
        profileInfo.setStyle(menuStyle);
        logoutItem.setStyle(menuStyle);

        profileInfo.setOnAction(e -> {
            new it.ispwproject.findyourbook.controller.gui.UserProfileGUI().show();
        });
        logoutItem.setOnAction(e -> onLogout.run());

        contextMenu.getItems().addAll(profileInfo, logoutItem);

        // Quando clicchi il bottone, facciamo apparire il menu esattamente sotto
        profileBtn.setOnMouseClicked(e -> {
            contextMenu.show(profileBtn, javafx.geometry.Side.BOTTOM, 0, 5);
        });

        return profileBtn;
    }
}