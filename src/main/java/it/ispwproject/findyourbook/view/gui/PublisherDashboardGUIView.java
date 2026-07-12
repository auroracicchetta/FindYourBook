package it.ispwproject.findyourbook.view.gui;

import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PublisherDashboardGUIView extends DashboardGUIView {

    public BorderPane buildRoot(String companyName, Runnable onLogout, Runnable onViewCatalog, Runnable onAddBook, Runnable onStats) {

        BorderPane root = new BorderPane();
        root.getStyleClass().add("fyb-background");

        try {
            root.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        } catch (Exception e) {
            AppLogger.logError("CSS non trovato in PublisherDashboardGUIView");
        }

        HBox navbar = new HBox(15);
        navbar.setPadding(new Insets(15, 30, 15, 30));
        navbar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("FindYourBook");
        title.getStyleClass().add("brand-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button profileMenu = createAvatar(companyName, onLogout);

        navbar.getChildren().addAll(title, spacer, profileMenu);
        root.setTop(navbar);

        VBox centerArea = new VBox(40);
        centerArea.setAlignment(Pos.TOP_CENTER);
        centerArea.setPadding(new Insets(60, 0, 0, 0));

        Label welcomeTitle = new Label("Benvenuto, " + companyName + "!");
        welcomeTitle.getStyleClass().add("title-label");

        Label subtitle = new Label("Cosa vuoi fare oggi?");
        subtitle.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 24px; -fx-font-style: italic; -fx-text-fill: #3A352F;");

        HBox actionCardsBox = new HBox(40);
        actionCardsBox.setAlignment(Pos.CENTER);

        Button btnCatalogo = createSquareButton("+ Gestisci Catalogo", onViewCatalog);
        Button btnPubblica = createSquareButton("📝 Pubblica Libro", onAddBook);
        Button btnStats    = createSquareButton("📊 Statistiche", onStats);

        actionCardsBox.getChildren().addAll(btnCatalogo, btnPubblica, btnStats);

        centerArea.getChildren().addAll(welcomeTitle, subtitle, actionCardsBox);
        root.setCenter(centerArea);

        return root;
    }

    private Button createSquareButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setPrefSize(260, 260);
        btn.setWrapText(true);
        btn.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        String defaultStyle = "-fx-background-color: #FFFFFF; -fx-text-fill: #3A352F; -fx-font-family: 'Arial'; -fx-font-size: 22px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 15, 0, 0, 5); -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: #FFFFFF; -fx-text-fill: #8AAB8F; -fx-font-family: 'Arial'; -fx-font-size: 22px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-color: #8AAB8F; -fx-border-width: 3; -fx-border-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5); -fx-cursor: hand;";

        btn.setStyle(defaultStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(defaultStyle));
        btn.setOnAction(e -> action.run());

        return btn;
    }
}