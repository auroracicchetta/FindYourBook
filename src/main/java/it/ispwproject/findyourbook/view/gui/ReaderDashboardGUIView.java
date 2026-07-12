package it.ispwproject.findyourbook.view.gui;

import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import java.util.function.Consumer;

public class ReaderDashboardGUIView extends DashboardGUIView {

    private static final String STYLE_BG = "-fx-background-color: " + BG_COLOR + ";";

    public VBox buildRoot(String username, Runnable onLogout, Runnable onMyBooksClick, Consumer<String> onSearch, Consumer<String> onGenreClick) {
        VBox root = new VBox(40);
        root.setPadding(new Insets(20, 50, 40, 50));
        root.setStyle(STYLE_BG);

        HBox navbar = super.buildNavbar(username, onMyBooksClick, onLogout, onSearch);

        Region sep = new Region();
        sep.setMinHeight(2);
        sep.setStyle("-fx-background-color: #FFFFFF;");

        root.getChildren().addAll(
                navbar,
                sep,
                buildGenresSection(onGenreClick)
        );

        try {
            root.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        } catch (Exception e) {
            AppLogger.logError("CSS non trovato sulla Home");
        }

        return root;
    }

    private VBox buildGenresSection(Consumer<String> onGenreClick) {
        VBox section = new VBox(40);
        section.setPadding(new Insets(10, 0, 0, 0));
        section.setAlignment(Pos.CENTER);

        Label title = new Label("Esplora il catalogo per genere");
        title.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 32px; -fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: bold;");


        HBox rowTop = new HBox(80);
        rowTop.setAlignment(Pos.CENTER);
        String[] generiTop = {"classici", "fantasy", "romance", "gialli"};
        for (String g : generiTop) {
            String nomeFormattato = g.substring(0, 1).toUpperCase() + g.substring(1);
            rowTop.getChildren().add(buildHugeGenreTile(g + ".png", nomeFormattato, onGenreClick));
        }

        HBox rowBottom = new HBox(80);
        rowBottom.setAlignment(Pos.CENTER);
        String[] generiBottom = {"avventura", "poesia", "storici", "filosofici"};
        for (String g : generiBottom) {
            String nomeFormattato = g.substring(0, 1).toUpperCase() + g.substring(1);
            rowBottom.getChildren().add(buildHugeGenreTile(g + ".png", nomeFormattato, onGenreClick));
        }

        section.getChildren().addAll(title, rowTop, rowBottom);
        return section;
    }

    private VBox buildHugeGenreTile(String imageName, String genreName, Consumer<String> onClick) {

        VBox tile = super.buildGenreTile(imageName, genreName, onClick);

        tile.setSpacing(15);

        for (javafx.scene.Node node : tile.getChildren()) {
            if (node instanceof javafx.scene.image.ImageView) {
                javafx.scene.image.ImageView icon = (javafx.scene.image.ImageView) node;

                icon.setFitWidth(130);
                icon.setFitHeight(130);
            } else if (node instanceof Label) {
                Label label = (Label) node;
                label.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 24px; -fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: bold;");
            }
        }

        return tile;
    }
}