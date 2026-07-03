package it.ispwproject.findyourbook.view.gui;

import it.ispwproject.findyourbook.util.logger.AppLogger; // Import aggiunto
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginGUIView {

    // Fields
    public final TextField usernameField = new TextField();
    public final PasswordField passwordField = new PasswordField();
    public final TextField visiblePasswordField = new TextField();
    public final Label errorLabel = new Label("");
    public final Button loginBtn = new Button("Accedi");
    public final Button registerBtn = new Button("Registrati");

    public LoginGUIView() {
        usernameField.getStyleClass().add("text-field");
        passwordField.getStyleClass().add("password-field");
        visiblePasswordField.getStyleClass().add("text-field");

        loginBtn.getStyleClass().add("button");
        errorLabel.getStyleClass().add("error-label");

        registerBtn.getStyleClass().clear();
        registerBtn.getStyleClass().add("secondary-button");

        usernameField.setPrefSize(400, 50);
        passwordField.setPrefSize(400, 50);
        visiblePasswordField.setPrefSize(400, 50);
        loginBtn.setPrefSize(400, 50);
        registerBtn.setPrefSize(400, 50);

        visiblePasswordField.setVisible(false);
        visiblePasswordField.managedProperty().bind(visiblePasswordField.visibleProperty());
        passwordField.managedProperty().bind(passwordField.visibleProperty());
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    public VBox buildRoot(Runnable onLogin, Runnable onRegister) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(30, 50, 50, 50));

        try {
            root.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        } catch (Exception e) {
            // Risolto code smell: Usato il logger al posto di System.err
            AppLogger.logError("CSS non trovato nel Login");
        }
        root.getStyleClass().add("fyb-background");

        Label brand = new Label("FindYourBook");
        brand.getStyleClass().add("brand-label");
        HBox topBar = new HBox(brand);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(0, 0, 40, 0));

        Label title = new Label("Accedi");
        title.getStyleClass().add("title-label");

        Label userLabel = new Label("Username");
        userLabel.getStyleClass().add("field-label");
        VBox userBox = new VBox(8, userLabel, usernameField);
        userBox.setMaxWidth(400);

        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("field-label");

        StackPane passwordBox = new StackPane(passwordField, visiblePasswordField);

        CheckBox showPasswordCheck = new CheckBox("Mostra password");
        showPasswordCheck.getStyleClass().add("check-box");
        showPasswordCheck.setPadding(new Insets(10, 0, 0, 0));

        showPasswordCheck.selectedProperty().addListener((obs, oldVal, show) -> {
            visiblePasswordField.setVisible(show);
            passwordField.setVisible(!show);
        });

        VBox passSection = new VBox(8, passLabel, passwordBox, showPasswordCheck);
        passSection.setMaxWidth(400);

        Label divider = new Label("───  Sei nuovo su FindYourBook ?  ───");
        divider.setStyle("-fx-font-size: 16px; -fx-text-fill: #3A352F; -fx-font-weight: bold;");
        VBox.setMargin(divider, new Insets(15, 0, 10, 0));

        loginBtn.setOnAction(e -> onLogin.run());
        registerBtn.setOnAction(e -> onRegister.run());

        VBox centerForm = new VBox(25);
        centerForm.setAlignment(Pos.CENTER);
        centerForm.getChildren().addAll(title, userBox, passSection, errorLabel, loginBtn, divider, registerBtn);

        root.getChildren().addAll(topBar, centerForm);
        javafx.application.Platform.runLater(() -> root.requestFocus());
        return root;
    }

    public void setError(String message) {
        errorLabel.setText(message);
    }
}