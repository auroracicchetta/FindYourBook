package it.ispwproject.findyourbook.controller.gui;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.controller.applicativo.PublisherController;
import it.ispwproject.findyourbook.view.gui.PublishBookGUIView;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class PublishBookGUI {

    private final Stage stage;
    private final PublishBookGUIView view = new PublishBookGUIView();
    private final String companyName;  // <--- AGGIUNTO
    private final Runnable onLogout;   // <--- AGGIUNTO

    public PublishBookGUI(Stage stage, String companyName, Runnable onLogout) {
        this.stage = stage;
        this.companyName = companyName;
        this.onLogout = onLogout;
    }

    public void show() {
        Parent root = view.buildRoot(companyName, this::goBackToDashboard, this::handlePublish, onLogout);
        Scene scene = GUIUtils.createScene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void goBackToDashboard() {
        new PublisherDashboardGUI(stage, companyName, onLogout).show();
    }

    private void handlePublish() {
        String title = view.titleField.getText().trim();
        String author = view.authorField.getText().trim();
        String genre = view.genreBox.getValue();
        String description = view.descArea.getText().trim();
        String coverUrl = view.coverUrlField.getText().trim();

        if (title.isEmpty() || author.isEmpty() || genre == null) {
            showAlert("Errore", "I campi Titolo, Autore e Genere sono obbligatori.");
            return;
        }

        BookBean newBookBean = new BookBean();
        newBookBean.setTitle(title);
        newBookBean.setAuthor(author);
        newBookBean.setGenre(genre.toLowerCase());
        newBookBean.setDescription(description);
        newBookBean.setImageUrl(coverUrl);
        newBookBean.setRating(0);

        try {
            PublisherController appController = new PublisherController();
            appController.publishNewBook(newBookBean);

            showAlert("Successo", "Il libro '" + title + "' è stato pubblicato nel tuo catalogo!");
            goBackToDashboard();
        } catch (Exception e) {
            AppLogger.logError("Errore durante la pubblicazione: " + e.getMessage());
            showAlert("Errore", "Impossibile pubblicare il libro. Riprova più tardi.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(title.equals("Successo") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}