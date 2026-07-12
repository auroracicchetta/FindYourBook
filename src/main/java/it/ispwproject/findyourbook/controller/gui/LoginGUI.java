package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.controller.applicativo.LoginController;
import it.ispwproject.findyourbook.controller.applicativo.LoginController.LoginResult;
import it.ispwproject.findyourbook.view.gui.LoginGUIView;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.exception.LoginException;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginGUI {

    private final Stage stage;
    private final LoginController loginController = new LoginController();
    private final LoginGUIView view  = new LoginGUIView();

    public LoginGUI(Stage stage) { this.stage = stage; }

    public void show() {
        // Creiamo il layout
        javafx.scene.layout.VBox root = view.buildRoot(this::handleLogin, () -> new RegistrationGUI(stage).show());

        // Usiamo il nostro GUIUtils che contiene il CSS
        Scene scene = GUIUtils.createScene(root);

        stage.setScene(scene);
        stage.show();
    }

    private void handleLogin() {
        String username = view.usernameField.getText().trim();
        String password = view.passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            view.setError("Inserisci username e password.");
            return;
        }

        try {
            LoginResult result = loginController.login(username, password);

            // Allineato ai nuovi Enum in Inglese!
            switch (result) {
                case SUCCESSO_READER -> MainGUI.showReaderDashboard();
                case SUCCESSO_PUBLISHER -> MainGUI.showPublisherDashboard();
                case SUCCESSO_ADMIN -> view.setError("Dashboard Admin in costruzione!");
            }

        } catch (LoginException e) {
            // Gestione Specifica 1: L'utente ha sbagliato i dati
            view.setError(e.getMessage());
        } catch (DAOException e) {
            // Gestione Specifica 2: Il DB o il sistema ha un problema
            view.setError("Errore di sistema. Riprova più tardi.");
        }
    }
}