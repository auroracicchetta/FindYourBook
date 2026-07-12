package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.controller.applicativo.UserController;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.view.gui.UserProfileGUIView;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class UserProfileGUI {

    public void show() {
        Stage profileStage = new Stage();
        UserProfileGUIView view = new UserProfileGUIView();

        try {
            var user = SessionManager.getInstance().getLoggedUser();
            String username = user.getUsername();
            String email = user.getEmail();

            LocalDate regDateObj = user.getRegistrationDate();
            String regDate = "Data sconosciuta";
            if (regDateObj != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ITALIAN);
                regDate = regDateObj.format(formatter);
            }

            var root = view.buildRoot(username, email, regDate, this::handleUpdateEmail);

            Scene scene = new Scene(root, 380, 450);
            profileStage.setTitle("Profilo Utente");
            profileStage.setScene(scene);
            profileStage.show();

        } catch (Exception e) {
            AppLogger.logError("Errore durante il caricamento del profilo: " + e.getMessage());
        }
    }

    private void handleUpdateEmail(String newEmail) {
        try {
            UserController userController = new UserController();
            userController.updateEmail(newEmail);

            showAlert(Alert.AlertType.INFORMATION, "Successo", "La tua email è stata aggiornata con successo a:\n" + newEmail);
        } catch (Exception e) {
            AppLogger.logWarning("Errore durante l'aggiornamento email: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Errore", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}