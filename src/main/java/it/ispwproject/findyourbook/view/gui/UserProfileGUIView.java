package it.ispwproject.findyourbook.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class UserProfileGUIView {

    public VBox buildRoot(String username, String email, String registrationDate) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #EBE2D4;"); // Il tuo colore panna/nocciola

        Label title = new Label("Il mio profilo");
        title.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4A3F35;");

        VBox infoBox = new VBox(15);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setPadding(new Insets(20));
        infoBox.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        addInfoRow(infoBox, "Username:", username);
        addInfoRow(infoBox, "Email:", email);
        addInfoRow(infoBox, "Registrato dal:", registrationDate);

        root.getChildren().addAll(title, infoBox);
        return root;
    }

    private void addInfoRow(VBox container, String label, String value) {
        VBox row = new VBox(5);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 12px; -fx-text-fill: #7A7A7A;");
        Label val = new Label(value);
        val.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 16px; -fx-text-fill: #4A3F35; -fx-font-weight: bold;");
        row.getChildren().addAll(lbl, val);
        container.getChildren().add(row);
    }
}