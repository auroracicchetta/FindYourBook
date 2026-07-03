package it.ispwproject.findyourbook.controller.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public final class GUIUtils {

    private GUIUtils() {
    }

    public static Scene createScene(Parent root) {

        // Creiamo la scena usando le dimensioni del tuo MainGUI
        Scene scene = new Scene(
                root,
                MainGUI.WINDOW_WIDTH,
                MainGUI.WINDOW_HEIGHT
        );

        // IL COLLEGAMENTO MAGICO: diciamo a JavaFX di usare il tuo nuovo CSS
        try {
            String cssPath = GUIUtils.class.getResource("/styles/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            AppLogger.logError("ATTENZIONE: File style.css non trovato nella cartella /css/");
        }

        return scene;
    }
}