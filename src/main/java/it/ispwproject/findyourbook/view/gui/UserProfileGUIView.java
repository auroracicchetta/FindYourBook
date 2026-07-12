package it.ispwproject.findyourbook.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;

public class UserProfileGUIView {

    public VBox buildRoot(String username, String email, String registrationDate, Consumer<String> onUpdateEmail) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #EFE8D8;"); // Il tuo colore panna/nocciola

        Label title = new Label("Il mio profilo");
        title.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #3A352F;");

        VBox infoBox = new VBox(15);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setPadding(new Insets(20));
        infoBox.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        addInfoRow(infoBox, "Username:", username);
        addInfoRow(infoBox, "Registrato dal:", registrationDate);

        VBox emailRow = new VBox(8);
        Label emailLbl = new Label("Email:");
        emailLbl.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 12px; -fx-text-fill: #7A7A7A;");

        HBox emailInputBox = new HBox(10);
        emailInputBox.setAlignment(Pos.CENTER_LEFT);

        TextField emailField = new TextField(email);
        emailField.getStyleClass().add("text-field");
        emailField.setPrefWidth(200);

        Button updateBtn = new Button("Aggiorna");
        updateBtn.getStyleClass().add("button");

        updateBtn.setOnAction(e -> onUpdateEmail.accept(emailField.getText()));

        emailInputBox.getChildren().addAll(emailField, updateBtn);
        emailRow.getChildren().addAll(emailLbl, emailInputBox);

        infoBox.getChildren().add(emailRow);

        root.getChildren().addAll(title, infoBox);
        return root;
    }

    private void addInfoRow(VBox container, String label, String value) {
        VBox row = new VBox(5);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 12px; -fx-text-fill: #7A7A7A;");
        Label val = new Label(value);
        val.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 16px; -fx-text-fill: #3A352F; -fx-font-weight: bold;");
        row.getChildren().addAll(lbl, val);
        container.getChildren().add(row);
    }
}