package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.view.gui.UserProfileGUIView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UserProfileGUI {

    public void show() {
        Stage profileStage = new Stage();
        UserProfileGUIView view = new UserProfileGUIView();

        // Recuperiamo i dati reali dal SessionManager
        var user = SessionManager.getInstance().getLoggedUser();
        String username = user.getUsername();
        String email = "utente@email.it"; // Se hai il campo email nel modello, usalo qui
        String regDate = "04 Luglio 2026"; // Se hai la data, passala pure!

        var root = view.buildRoot(username, email, regDate);

        Scene scene = new Scene(root, 350, 400);
        profileStage.setTitle("Profilo Utente");
        profileStage.setScene(scene);
        profileStage.show();
    }
}