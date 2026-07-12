package it.ispwproject.findyourbook.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PublishBookGUIView extends DashboardGUIView {

    public final TextField titleField = new TextField();
    public final TextField authorField = new TextField();
    public final ComboBox<String> genreBox = new ComboBox<>();
    public final TextArea descArea = new TextArea();
    public final TextField coverUrlField = new TextField();

    private static final String BG_COLOR = "#EFE8D8";
    private static final String CARD_BG = "#FFFFFF";
    private static final String TEXT_DARK = "#3A352F";
    private static final String STYLE_BG_COLOR = "-fx-background-color: ";

    private static final String BTN_BASE_STYLE = "-fx-background-color: #8AAB8F; -fx-text-fill: white; -fx-font-family: 'Georgia'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 30; -fx-cursor: hand;";
    private static final String BTN_HOVER_STYLE = "-fx-background-color: #6C8B70; -fx-text-fill: white; -fx-font-family: 'Georgia'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 30; -fx-cursor: hand;";

    public VBox buildRoot(String companyName, Runnable onCancel, Runnable onPublish, Runnable onLogout) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20, 50, 30, 50));
        root.setStyle(STYLE_BG_COLOR + BG_COLOR + ";");
        root.setAlignment(Pos.TOP_CENTER);

        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("< Indietro");
        String baseStyle = "-fx-background-color: transparent; -fx-text-fill: #4A3F35; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 0;";
        backBtn.setStyle(baseStyle);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(baseStyle + " -fx-underline: true;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(baseStyle));
        backBtn.setOnAction(e -> onCancel.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button profileMenu = createAvatar(companyName, onLogout);

        topBar.getChildren().addAll(backBtn, spacer, profileMenu);

        Label headerLabel = new Label("Pubblica un nuovo libro");
        headerLabel.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        Label subHeader = new Label("Compila i dettagli dell'opera da aggiungere al catalogo aziendale.");
        subHeader.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px; -fx-text-fill: #70675C;");

        VBox headerBox = new VBox(5, headerLabel, subHeader);
        headerBox.setAlignment(Pos.CENTER);
        VBox.setMargin(headerBox, new Insets(10, 0, 10, 0));

        // 2. Form (Card centrale)
        HBox formContainer = new HBox(40);
        formContainer.setPadding(new Insets(30));
        formContainer.setStyle(STYLE_BG_COLOR + CARD_BG + "; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");
        formContainer.setMaxWidth(800);

        VBox leftCol = new VBox(15);
        leftCol.setAlignment(Pos.TOP_CENTER);
        leftCol.setPrefWidth(250);

        Label coverPlaceholder = new Label("Nessuna Copertina");
        coverPlaceholder.setPrefSize(180, 270);
        coverPlaceholder.setAlignment(Pos.CENTER);
        coverPlaceholder.setStyle("-fx-background-color: #D3C5B1; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10;");

        Label urlLabel = createFieldLabel("URL Copertina:");
        coverUrlField.setPromptText("https://esempio.com/copertina.jpg");
        styleTextField(coverUrlField);

        leftCol.getChildren().addAll(coverPlaceholder, urlLabel, coverUrlField);

        VBox rightCol = new VBox(15);
        rightCol.setPrefWidth(450);

        Label titleLabel = createFieldLabel("Titolo:");
        styleTextField(titleField);

        Label authorLabel = createFieldLabel("Autore:");
        styleTextField(authorField);

        Label genreLabel = createFieldLabel("Genere:");
        genreBox.getItems().addAll(
                "Classici",
                "Fantasy",
                "Romance",
                "Gialli",
                "Avventura",
                "Poesia",
                "Storici",
                "Filosofici"
        );
        genreBox.setPrefWidth(Double.MAX_VALUE);
        genreBox.setStyle("-fx-background-color: white; -fx-border-color: #D3C5B1; -fx-border-radius: 8; -fx-padding: 2;");

        Label descLabel = createFieldLabel("Trama:");
        descArea.setPromptText("Scrivi una breve descrizione del libro...");
        descArea.setWrapText(true);
        descArea.setPrefHeight(150);
        descArea.setStyle("-fx-control-inner-background: white; -fx-border-color: #D3C5B1; -fx-border-radius: 8;");

        rightCol.getChildren().addAll(
                titleLabel, titleField,
                authorLabel, authorField,
                genreLabel, genreBox,
                descLabel, descArea
        );

        formContainer.getChildren().addAll(leftCol, rightCol);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setMaxWidth(800);
        VBox.setMargin(buttonBox, new Insets(20, 0, 0, 0));

        Button publishBtn = new Button("Pubblica Libro");
        publishBtn.setStyle(BTN_BASE_STYLE);

        publishBtn.setOnMouseEntered(e -> publishBtn.setStyle(BTN_HOVER_STYLE));
        publishBtn.setOnMouseExited(e -> publishBtn.setStyle(BTN_BASE_STYLE));

        publishBtn.setOnAction(e -> onPublish.run());

        buttonBox.getChildren().add(publishBtn);

        root.getChildren().addAll(topBar, headerBox, formContainer, buttonBox);
        return root;
    }

    private Label createFieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        return lbl;
    }

    private void styleTextField(TextField field) {
        field.setStyle("-fx-background-color: white; -fx-border-color: #D3C5B1; -fx-border-radius: 8; -fx-padding: 8;");
    }
}