package it.ispwproject.findyourbook.controller.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public final class GUIUtils {

    private GUIUtils() {
    }

    public static Scene createScene(Parent root) {

        Scene scene = new Scene(
                root,
                MainGUI.WINDOW_WIDTH,
                MainGUI.WINDOW_HEIGHT
        );

        try {
            String cssPath = GUIUtils.class.getResource("/styles/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            AppLogger.logError("ATTENZIONE: File style.css non trovato nella cartella /css/");
        }

        return scene;
    }
}