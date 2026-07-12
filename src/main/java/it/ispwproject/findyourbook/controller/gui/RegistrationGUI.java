package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.bean.RegistrationBean;
import it.ispwproject.findyourbook.controller.applicativo.RegistrationController;
import it.ispwproject.findyourbook.enumerator.Role;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.exception.RegistrationException;
import it.ispwproject.findyourbook.view.gui.RegistrationGUIView;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegistrationGUI {
    private final Stage stage;
    private final RegistrationController registrationController = new RegistrationController();
    private final RegistrationGUIView view = new RegistrationGUIView();

    public RegistrationGUI(Stage stage) { this.stage = stage; }

    public void show() {
        Parent root = view.buildRoot(
                () -> new LoginGUI(stage).show(),
                this::handleRegistration
        );

        Scene scene = GUIUtils.createScene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void handleRegistration() {
        RegistrationBean bean = new RegistrationBean();
        bean.setName(view.nameField.getText().trim());
        bean.setSurname(view.surnameField.getText().trim());
        bean.setUsername(view.usernameField.getText().trim());
        bean.setEmail(view.emailField.getText().trim());
        bean.setPassword(view.passwordField.getText().trim());
        bean.setConfirmPassword(view.confirmPasswordField.getText().trim());

        bean.setBirthDate(view.dataNascita.getValue());


        bean.setRole(view.casaEditriceRadio.isSelected() ? Role.PUBLISHER : Role.READER);

        if (view.casaEditriceRadio.isSelected()) {
            bean.setDescription(view.descrizioneField.getText().trim());
        }

        try {
            AppLogger.logInfo("--- INIZIO REGISTRAZIONE ---");
            registrationController.register(bean);
            AppLogger.logInfo("--- UTENTE SALVATO NEL DATABASE ---");

            view.showInformationSuccess(
                    "Registrazione Completata",
                    "Il tuo account è stato creato con successo. Clicca OK per tornare al login."
            );

            view.clearFields();
            new LoginGUI(stage).show();

        } catch (RegistrationException e) {
            AppLogger.logWarning("Errore di validazione: " + e.getMessage());
            view.setError(e.getMessage());
        } catch (DAOException e) {
            AppLogger.logError("Errore di sistema: " + e.getMessage());
            view.setError("Errore di sistema durante la registrazione. Riprova più tardi.");
        }
    }
}