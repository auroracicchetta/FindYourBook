package it.ispwproject.findyourbook.view.gui;

import it.ispwproject.findyourbook.bean.PublisherStatsBean;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Map;

public class PublisherStatsGUIView extends DashboardGUIView {

    private static final String BG_COLOR = "#EFE8D8";
    private static final String CARD_BG = "#FFFFFF";
    private static final String TEXT_DARK = "#3A352F";
    private static final String ACCENT_GREEN = "#8AAB8F";

    public final BarChart<String, Number> barChart;

    private final Label totalBooksLbl = new Label("0");
    private final Label totalReadLbl = new Label("0");
    private final Pane labelOverlay = new Pane();

    public PublisherStatsGUIView() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Titolo Libro");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Numero di Letture");

        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("I 4 Libri più Letti dagli Utenti");
        barChart.setLegendVisible(false);
        barChart.setStyle("-fx-font-family: 'Georgia';");
    }


    public BorderPane buildRoot(String companyName, Runnable onBack, Runnable onLogout) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");
        root.setPadding(new Insets(30, 50, 30, 50));

        try {
            root.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        } catch (Exception e) {
            AppLogger.logError("CSS non trovato in PublisherStatsGUIView");
        }

        // Top Bar con avatar
        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Pulsante Indietro
        Label backBtn = new Label("< Indietro");
        String defaultStyle = "-fx-text-fill: " + TEXT_DARK + "; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-padding: 0;";
        String hoverStyle = defaultStyle + " -fx-underline: true;";
        backBtn.setStyle(defaultStyle);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(hoverStyle));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(defaultStyle));
        backBtn.setOnMouseClicked(e -> onBack.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button profileMenu = createAvatar(companyName, onLogout);

        topBar.getChildren().addAll(backBtn, spacer, profileMenu);

        // ─── TITOLO ───
        Label title = new Label("Report Aziendale");
        title.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        VBox.setMargin(title, new Insets(10, 0, 20, 0));

        // ─── HEADER ───
        VBox header = new VBox(topBar, title);
        header.setAlignment(Pos.TOP_LEFT);
        root.setTop(header);

        // ─── STATISTICHE ───
        HBox summaryBox = new HBox(30,
                createStatCard("Libri a Catalogo", totalBooksLbl),
                createStatCard("Totale Libri Letti", totalReadLbl)
        );
        summaryBox.setAlignment(Pos.CENTER);

        labelOverlay.setMouseTransparent(true);
        labelOverlay.prefWidthProperty().bind(barChart.widthProperty());
        labelOverlay.prefHeightProperty().bind(barChart.heightProperty());
        StackPane chartStack = new StackPane(barChart, labelOverlay);

        VBox centerBox = new VBox(30, summaryBox, chartStack);
        centerBox.setPadding(new Insets(10, 0, 0, 0));
        root.setCenter(centerBox);

        return root;
    }

    private VBox createStatCard(String title, Label valueLabel) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(200, 80);
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 14px; -fx-text-fill: #70675C;");

        valueLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + ACCENT_GREEN + ";");

        card.getChildren().addAll(titleLbl, valueLabel);
        return card;
    }

    public void updateView(PublisherStatsBean statsBean) {
        totalBooksLbl.setText(String.valueOf(statsBean.getTotalBooksPublished()));
        totalReadLbl.setText(String.valueOf(statsBean.getTotalBooksRead()));

        labelOverlay.getChildren().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : statsBean.getTopReadBooks().entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().clear();
        barChart.getData().add(series);

        for (XYChart.Data<String, Number> data : series.getData()) {
            data.getNode().setStyle("-fx-bar-fill: " + ACCENT_GREEN + ";");
            addValueLabel(data);
        }
    }


    private void addValueLabel(XYChart.Data<String, Number> data) {
        Label valueLbl = new Label(String.valueOf(data.getYValue()));
        valueLbl.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        labelOverlay.getChildren().add(valueLbl);

        Node barNode = data.getNode();

        Runnable reposition = () -> {
            if (barNode.getScene() == null) return;
            Bounds boundsInScene = barNode.localToScene(barNode.getBoundsInLocal());
            Bounds boundsInOverlay = labelOverlay.sceneToLocal(boundsInScene);
            valueLbl.setLayoutX(boundsInOverlay.getMinX() + boundsInOverlay.getWidth() / 2 - 14);
            valueLbl.setLayoutY(boundsInOverlay.getMinY() - 16);
        };

        barNode.boundsInLocalProperty().addListener((obs, oldB, newB) -> reposition.run());
        barNode.layoutXProperty().addListener((obs, oldX, newX) -> reposition.run());
        barNode.layoutYProperty().addListener((obs, oldY, newY) -> reposition.run());

        Platform.runLater(reposition);
    }
}