package it.ispwproject.findyourbook.view.gui;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.util.logger.AppLogger; // Import aggiunto
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class ReaderDashboardGUIView extends DashboardGUIView {

    // Costanti per evitare la duplicazione delle stringhe CSS
    private static final String STYLE_BG = "-fx-background-color: " + BG_COLOR + ";";

    public VBox buildRoot(Runnable onLogout, Runnable onMyBooksClick, Consumer<String> onSearch, Consumer<String> onGenreClick) {
        VBox root = new VBox(40);
        root.setPadding(new Insets(20, 50, 40, 50));
        root.setStyle(STYLE_BG);

        // 1. Usa la Navbar del padre
        HBox navbar = super.buildNavbar(onMyBooksClick, onLogout, onSearch);

        // Separatore
        Region sep = new Region();
        sep.setMinHeight(2);
        sep.setStyle("-fx-background-color: #FFFFFF;");

        // 2. Assembla la schermata usando i moduli
        root.getChildren().addAll(
                navbar,
                sep,
                buildRecommendationsSection(),
                buildGenresSection(onGenreClick)
        );

        try {
            root.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        } catch (Exception e) {
            // Risolto code smell: Logger al posto di System.err
            AppLogger.logError("CSS non trovato sulla Home");
        }

        return root;
    }

    private HBox buildRecommendationsSection() {
        HBox section = new HBox(40);
        section.setAlignment(Pos.CENTER);

        BookBean book1 = new BookBean("Il cerchio dei giorni", "Ken Follett", null, null, null);
        BookBean book2 = new BookBean("La bugia dell'orchidea", "Donato Carrisi", null, null, null);

        VBox card1 = super.buildBookCard("Il nostro consiglio", book1, null, null, null, null);
        VBox card2 = super.buildBookCard("Le novità dalle Case Editrici", book2, null, null, null, null);

        section.getChildren().addAll(card1, card2);
        return section;
    }

    private VBox buildGenresSection(Consumer<String> onGenreClick) {
        VBox section = new VBox(30);
        section.setPadding(new Insets(40, 0, 0, 0));
        section.setAlignment(Pos.CENTER);

        Label title = new Label("Seleziona per Genere");
        title.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 22px; -fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: bold;");

        HBox iconsBox = new HBox(35);
        iconsBox.setAlignment(Pos.CENTER);

        String[] generi = {"classici", "fantasy", "romance", "gialli", "avventura", "poesia", "storici", "filosofici"};
        for (String g : generi) {
            String nomeFormattato = g.substring(0, 1).toUpperCase() + g.substring(1);
            iconsBox.getChildren().add(super.buildGenreTile(g + ".png", nomeFormattato, onGenreClick));
        }

        section.getChildren().addAll(title, iconsBox);
        return section;
    }
}