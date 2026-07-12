package it.ispwproject.findyourbook.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public class PublisherCatalogGUIView extends DashboardGUIView {

    private static final String BG_COLOR = "#EFE8D8";
    private static final String CARD_BG = "#FFFFFF";
    private static final String TEXT_DARK = "#3A352F";
    private static final String ACCENT_GREEN = "#8AAB8F";

    public final FlowPane catalogGrid = new FlowPane();

    public BorderPane buildRoot(String companyName, Runnable onBack, Runnable onLogout,
                                java.util.function.Consumer<String> onSearchAction) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");
        root.getStyleClass().add("fyb-background");

        try {
            root.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        } catch (Exception e) {
            AppLogger.logError("CSS non trovato in PublisherCatalogGUIView");
        }

        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(20, 30, 10, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("< Indietro");
        backBtn.getStyleClass().add("back-link-button");
        backBtn.setOnAction(e -> onBack.run());

        Label title = new Label("Il mio Catalogo");
        title.getStyleClass().add("title-label");

        TextField catalogSearchBar = new TextField();
        catalogSearchBar.setPromptText("Cerca nel tuo catalogo per titolo o autore...");
        catalogSearchBar.setPrefWidth(350);
        catalogSearchBar.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 20; -fx-padding: 10 20;");

        if (onSearchAction != null) {
            catalogSearchBar.textProperty().addListener((observable, oldValue, newValue) -> {
                onSearchAction.accept(newValue);
            });
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button profileMenu = createAvatar(companyName, onLogout);

        topBar.getChildren().addAll(backBtn, title, catalogSearchBar, spacer, profileMenu);
        root.setTop(topBar);

        // 2. CENTRO (Griglia dei Libri)
        catalogGrid.setHgap(30);
        catalogGrid.setVgap(30);
        catalogGrid.setPadding(new Insets(20, 30, 30, 30));
        catalogGrid.setAlignment(Pos.TOP_LEFT);

        ScrollPane scroll = new ScrollPane(catalogGrid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.getStyleClass().add("transparent-pane");
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        root.setCenter(scroll);

        return root;
    }

    public void addCatalogCard(String title, String author, String imageUrl, int copieVendute, Runnable onClick) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 5); -fx-cursor: hand; -fx-padding: 20;");
        card.setPrefSize(200, 320); // Dimensioni fisse per evitare griglie disordinate
        card.setAlignment(Pos.TOP_CENTER);

        // Effetto Hover (si solleva e aumenta l'ombra)
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 15, 0, 0, 8); -fx-cursor: hand; -fx-padding: 20;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 5); -fx-cursor: hand; -fx-padding: 20;"));
        card.setOnMouseClicked(e -> onClick.run());

        // Gestione dell'Immagine
        ImageView coverView = new ImageView();
        coverView.setFitWidth(120);
        coverView.setFitHeight(180);
        coverView.setPreserveRatio(true);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // I booleani (true, true, true) indicano che l'immagine viene caricata in background!
                coverView.setImage(new Image(imageUrl, 120, 180, true, true, true));
            } catch (Exception e) {
                AppLogger.logError("Immagine non caricata: " + imageUrl);
            }
        } else {
            // Segnaposto se non c'è il link
            coverView.setStyle("-fx-background-color: #D3C5B1;");
        }

        // Informazioni Libro
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-family: 'Georgia'; -fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: " + TEXT_DARK + ";");
        titleLbl.setWrapText(true);
        titleLbl.setMaxHeight(40);
        titleLbl.setAlignment(Pos.CENTER);
        titleLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label authLbl = new Label("di " + author);
        authLbl.setStyle("-fx-font-family: 'Georgia'; -fx-text-fill: #70675C; -fx-font-size: 12px; -fx-font-style: italic;");

        infoBox.getChildren().addAll(titleLbl, authLbl);

        // Sezione Vendite
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox statsRow = new HBox();
        statsRow.setAlignment(Pos.CENTER);
        Label soldLbl = new Label("📈 Edit");
        soldLbl.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 12px; -fx-background-color: #E2EFE5; -fx-text-fill: " + ACCENT_GREEN + "; -fx-padding: 5 15; -fx-background-radius: 10; -fx-font-weight: bold;");
        statsRow.getChildren().add(soldLbl);

        card.getChildren().addAll(coverView, infoBox, spacer, statsRow);
        catalogGrid.getChildren().add(card);
    }

    public void clearGrid() {
        catalogGrid.getChildren().clear();
    }
}