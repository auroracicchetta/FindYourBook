package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.bean.RegistrationBean;
import it.ispwproject.findyourbook.controller.applicativo.RegistrationController;
import it.ispwproject.findyourbook.enumerator.Role;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.exception.RegistrationException;
import it.ispwproject.findyourbook.view.gui.RegistrationGUIView;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class RegistrationGUI {
    private final Stage stage;
    private final RegistrationController registrationController = new RegistrationController();
    private final RegistrationGUIView view = new RegistrationGUIView();

    public RegistrationGUI(Stage stage) { this.stage = stage; }

    public void show() {
        // Pass both the back action AND the registration action
        javafx.scene.Parent root = view.buildRoot(
                () -> new LoginGUI(stage).show(), // Action for 'Indietro'
                this::handleRegistration          // Action for 'Completa registrazione'
        );

        javafx.scene.Scene scene = it.ispwproject.findyourbook.controller.gui.GUIUtils.createScene(root);

        stage.setScene(scene);
        stage.show();
    }

    private void handleRegistration() {
        it.ispwproject.findyourbook.bean.RegistrationBean bean = new it.ispwproject.findyourbook.bean.RegistrationBean();
        bean.setName(view.nameField.getText().trim());
        bean.setSurname(view.surnameField.getText().trim());
        bean.setUsername(view.usernameField.getText().trim());
        bean.setPassword(view.passwordField.getText().trim());
        bean.setConfirmPassword(view.confirmPasswordField.getText().trim());

        // Prende la data
        bean.setDataNascita(view.dataNascita.getValue());

        bean.setRole(view.casaEditriceRadio.isSelected() ?
                it.ispwproject.findyourbook.enumerator.Role.CASA_EDITRICE :
                it.ispwproject.findyourbook.enumerator.Role.LETTORE);

        if (view.casaEditriceRadio.isSelected()) {
            bean.setDescrizione(view.descrizioneField.getText().trim());
        }

        try {
            AppLogger.logInfo("--- INIZIO REGISTRAZIONE ---");
            registrationController.register(bean);
            AppLogger.logInfo("--- UTENTE SALVATO NEL DATABASE ---");

            // 1. Mostra il popup di successo
            view.showInformationSuccess(
                    "Registrazione Completata",
                    "Il tuo account è stato creato con successo. Clicca OK per tornare al login."
            );

            // 2. Pulisce i campi del form
            view.clearFields();

            // 3. Ti riporta alla schermata di Login
            new LoginGUI(stage).show();

        } catch (Exception e) {
            AppLogger.logError("--- ERRORE DURANTE LA REGISTRAZIONE --- " + e.getMessage());
            view.setError(e.getMessage());
        }
    }

    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registrazione");
        alert.setContentText("Registrazione completata! Ora puoi effettuare il login.");
        alert.showAndWait();
        MainGUI.showLogin();
    }
}