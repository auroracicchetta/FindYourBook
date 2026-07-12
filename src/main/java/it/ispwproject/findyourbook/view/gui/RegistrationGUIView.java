package it.ispwproject.findyourbook.view.gui;

import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class RegistrationGUIView {

    // --- COSTANTI PER EVITARE DUPLICAZIONE STRINGHE ---
    private static final String CSS_TEXT_FIELD = "text-field";
    private static final String CSS_CHECK_BOX = "check-box";
    private static final String CSS_FIELD_LABEL = "field-label";

    public final TextField nameField = new TextField();
    public final TextField surnameField = new TextField();
    public final TextField usernameField = new TextField();
    public final TextField emailField = new TextField();
    public final DatePicker dataNascita = new DatePicker();
    public final TextField descrizioneField = new TextField();

    public final PasswordField passwordField = new PasswordField();
    public final TextField visiblePasswordField = new TextField();
    public final PasswordField confirmPasswordField = new PasswordField();
    public final TextField visibleConfirmPasswordField = new TextField();

    public final RadioButton lettoreRadio = new RadioButton("Lettore");
    public final RadioButton casaEditriceRadio = new RadioButton("Casa Editrice");
    public final ToggleGroup roleGroup = new ToggleGroup();

    public final Button registerBtn = new Button("Completa registrazione");
    public final Label errorLabel = new Label("");
    public final CheckBox showPasswordCheck = new CheckBox("Mostra password");

    public RegistrationGUIView() {
        // Applichiamo le costanti CSS
        nameField.getStyleClass().add(CSS_TEXT_FIELD);
        surnameField.getStyleClass().add(CSS_TEXT_FIELD);
        usernameField.getStyleClass().add(CSS_TEXT_FIELD);
        emailField.getStyleClass().add(CSS_TEXT_FIELD);
        passwordField.getStyleClass().add("password-field");
        confirmPasswordField.getStyleClass().add("password-field");
        descrizioneField.getStyleClass().add(CSS_TEXT_FIELD);

        visiblePasswordField.getStyleClass().add(CSS_TEXT_FIELD);
        visibleConfirmPasswordField.getStyleClass().add(CSS_TEXT_FIELD);

        registerBtn.getStyleClass().add("button");
        errorLabel.getStyleClass().add("error-label");
        lettoreRadio.getStyleClass().add(CSS_CHECK_BOX);
        casaEditriceRadio.getStyleClass().add(CSS_CHECK_BOX);
        showPasswordCheck.getStyleClass().add(CSS_CHECK_BOX);

        lettoreRadio.setToggleGroup(roleGroup);
        casaEditriceRadio.setToggleGroup(roleGroup);
        lettoreRadio.setSelected(true);

        visiblePasswordField.setVisible(false);
        visiblePasswordField.managedProperty().bind(visiblePasswordField.visibleProperty());
        passwordField.managedProperty().bind(passwordField.visibleProperty());
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        visibleConfirmPasswordField.setVisible(false);
        visibleConfirmPasswordField.managedProperty().bind(visibleConfirmPasswordField.visibleProperty());
        confirmPasswordField.managedProperty().bind(confirmPasswordField.visibleProperty());
        visibleConfirmPasswordField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

        showPasswordCheck.selectedProperty().addListener((obs, oldVal, show) -> {
            visiblePasswordField.setVisible(show);
            passwordField.setVisible(!show);
            visibleConfirmPasswordField.setVisible(show);
            confirmPasswordField.setVisible(!show);
        });
    }

    public VBox buildRoot(Runnable onBack, Runnable onRegister) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30, 50, 30, 50));
        root.setAlignment(Pos.CENTER);

        try {
            root.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        } catch (Exception e) {
            // Risolto code smell: Logger invece di System.err
            AppLogger.logError("CSS non trovato nella Registrazione");
        }

        root.getStyleClass().add("fyb-background");

        Label brand = new Label("FindYourBook");
        brand.getStyleClass().add("brand-label");

        // Pulsante Indietro (Grafica a pillola sicura e testo corretto)
        // Pulsante Indietro con effetto Sottolineatura al passaggio del mouse

        Button backBtn = new Button("< Indietro");

// La tua classe CSS che fa già tutto (compresa la sottolineatura all'hover!)
        backBtn.getStyleClass().add("back-link-button");

        backBtn.setOnAction(e -> onBack.run());

        backBtn.setOnAction(e -> onBack.run());

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setMaxWidth(900);
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(brand, spacer, backBtn);

        Label title = new Label("Registrazione");
        title.getStyleClass().add("title-label");
        VBox titleBox = new VBox(5, title);
        titleBox.setAlignment(Pos.CENTER);
        VBox.setMargin(titleBox, new Insets(30, 0, 10, 0));

        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        StackPane passBox = new StackPane(passwordField, visiblePasswordField);
        StackPane confPassBox = new StackPane(confirmPasswordField, visibleConfirmPasswordField);

        VBox dobContainer = createFieldBox("Data di Nascita", dataNascita);
        dobContainer.visibleProperty().bind(lettoreRadio.selectedProperty());
        dobContainer.managedProperty().bind(dobContainer.visibleProperty());

        // --- CREAZIONE DESCRIZIONE (Spostata qui per metterla nella griglia) ---
        VBox descBox = createFieldBox("Descrizione (solo Case Editrici)", descrizioneField);
        descrizioneField.visibleProperty().bind(casaEditriceRadio.selectedProperty());
        descBox.visibleProperty().bind(casaEditriceRadio.selectedProperty());
        descBox.managedProperty().bind(descBox.visibleProperty());

        // --- GRIGLIA SIMMETRICA 2x4 ---
        // Colonna 0 (Sinistra)
        grid.add(createFieldBox("Nome", nameField), 0, 0);
        grid.add(createFieldBox("Username", usernameField), 0, 1);
        grid.add(createFieldBox("Password", passBox), 0, 2);
        grid.add(dobContainer, 0, 3); // Si sovrappone a descBox
        grid.add(descBox, 0, 3);      // Si sovrappone a dobContainer

        // Colonna 1 (Destra)
        grid.add(createFieldBox("Cognome", surnameField), 1, 0);
        grid.add(createFieldBox("Email", emailField), 1, 1); // NUOVO CAMPO
        grid.add(createFieldBox("Conferma Password", confPassBox), 1, 2);

        // --- AGGIUNTA ISTRUZIONI PASSWORD ---
        VBox passwordInstructionsBox = new VBox(5);
        Label instructionsHeader = new Label("La password deve contenere:");
        instructionsHeader.setStyle("-fx-font-size: 13px; -fx-text-fill: #3A352F; -fx-font-weight: bold;"); // Sostituito #4A3F35 con #3A352F

        Label instruction1 = new Label("• Almeno 8 caratteri");
        Label instruction2 = new Label("• Almeno un carattere numerico");
        Label instruction3 = new Label("• Almeno una lettera maiuscola");

        String instructionStyle = "-fx-font-size: 12px; -fx-text-fill: #70675C; -fx-font-family: Arial, sans-serif;";
        instruction1.setStyle(instructionStyle);
        instruction2.setStyle(instructionStyle);
        instruction3.setStyle(instructionStyle);

        passwordInstructionsBox.getChildren().addAll(instructionsHeader, instruction1, instruction2, instruction3);
        passwordInstructionsBox.setPadding(new Insets(10, 0, 0, 10));

        // Aggiunta alla griglia (Destra, Riga 3)
        grid.add(passwordInstructionsBox, 1, 3);

        // --- RIGA 4: RUOLO (Sinx) e MOSTRA PASS (Destra) ---
        HBox roleBox = new HBox(20, new Label("Ruolo:"), lettoreRadio, casaEditriceRadio);
        roleBox.setAlignment(Pos.CENTER_LEFT);
        grid.add(roleBox, 0, 4);

        HBox checkContainer = new HBox(showPasswordCheck);
        checkContainer.setAlignment(Pos.TOP_LEFT);
        checkContainer.setPadding(new Insets(5, 0, 0, 10));
        grid.add(checkContainer, 1, 4);

        // --- BOTTONE FINALE ---
        registerBtn.setOnAction(e -> onRegister.run());
        registerBtn.setPrefWidth(300);

        // BottomBox ora è pulitissimo
        VBox bottomBox = new VBox(15, errorLabel, registerBtn);
        bottomBox.setAlignment(Pos.CENTER);
        VBox.setMargin(bottomBox, new Insets(30, 0, 0, 0));

        root.getChildren().addAll(header, titleBox, grid, bottomBox);
        return root;
    }

    private VBox createFieldBox(String labelText, Node field) {
        Label l = new Label(labelText);
        // Usa la costante definita in cima alla classe invece della stringa "field-label"
        l.getStyleClass().add(CSS_FIELD_LABEL);

        if (field instanceof Control) {
            ((Control) field).setPrefWidth(350);
            ((Control) field).setMinHeight(40);
        } else if (field instanceof Region) {
            ((Region) field).setPrefWidth(350);
            ((Region) field).setMinHeight(40);
        }
        return new VBox(5, l, field);
    }

    public void setError(String message) {
        errorLabel.setText(message);
    }

    public void clearFields() {
        nameField.clear();
        surnameField.clear();
        usernameField.clear();
        emailField.clear(); // <-- NUOVO
        passwordField.clear();
        confirmPasswordField.clear();
        descrizioneField.clear();
        dataNascita.setValue(null);
        lettoreRadio.setSelected(true);
        errorLabel.setText("");
    }

    public void showInformationSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (registerBtn.getScene() != null && registerBtn.getScene().getWindow() != null) {
            alert.initOwner(registerBtn.getScene().getWindow());
        }
        alert.showAndWait();
    }
}