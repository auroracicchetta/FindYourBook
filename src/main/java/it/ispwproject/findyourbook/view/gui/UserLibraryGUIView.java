package it.ispwproject.findyourbook.view.gui;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class UserLibraryGUIView extends DashboardGUIView {

    private FlowPane booksGrid;
    private Button btnToRead;
    private Button btnReading;
    private Button btnRead;

    private ReadingStatus currentActiveStatus = null;

    private static final String STYLE_INACTIVE = "library-status-btn";
    private static final String STYLE_ACTIVE = "library-status-btn-active";

    public VBox buildRoot(String username, int readBooksCount, Runnable onHomeClick, Runnable onLogout, Consumer<String> onSearch, Consumer<ReadingStatus> onFilterClick) {
        VBox root = new VBox(30);
        root.setPadding(new Insets(20, 50, 40, 50));
        root.getStyleClass().add("fyb-background");

        HBox navbar = super.buildNavbar(username, null, onLogout, onSearch);

        Label homeLabel = (Label) navbar.getChildren().get(1);
        homeLabel.getStyleClass().clear();
        homeLabel.getStyleClass().add("nav-link");
        homeLabel.setOnMouseClicked(e -> onHomeClick.run());

        Label myBooksLabel = (Label) navbar.getChildren().get(2);
        myBooksLabel.getStyleClass().clear();
        myBooksLabel.getStyleClass().add("nav-link-active");

        Region sep = new Region();
        sep.setMinHeight(2);
        sep.setStyle("-fx-background-color: #FFFFFF;");

        VBox headerBox = new VBox(35);
        headerBox.setAlignment(Pos.CENTER);

        HBox profileBox = new HBox(30);
        profileBox.setAlignment(Pos.CENTER);

        String initial = (username != null && !username.isEmpty()) ? username.substring(0, 1).toUpperCase() : "U";
        Label avatarIcon = new Label(initial);
        avatarIcon.setMinSize(100, 100);
        avatarIcon.setMaxSize(100, 100);
        avatarIcon.setAlignment(Pos.CENTER);
        avatarIcon.setStyle("-fx-background-color: #C1A68D; -fx-text-fill: white; -fx-font-family: 'Arial'; -fx-font-size: 45px; -fx-font-weight: bold; -fx-background-radius: 100;");

        Label nameLabel = new Label("La tua libreria, " + username + "!");
        nameLabel.setStyle("-fx-font-family: 'Georgia'; -fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");

        profileBox.getChildren().addAll(avatarIcon, nameLabel);

        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);

        Label stat1 = new Label("Libri letti: " + readBooksCount);
        stat1.setStyle("-fx-font-family: 'Georgia'; -fx-font-weight: bold; -fx-font-style: italic; -fx-font-size: 24px; -fx-text-fill: " + TEXT_DARK + ";");

        statsBox.getChildren().add(stat1);

        HBox filterBox = new HBox(40);
        filterBox.setAlignment(Pos.CENTER);

        btnToRead = new Button("Da leggere");
        btnReading = new Button("In lettura");
        btnRead = new Button("Letti");

        resetButtonColors();

        setupHover(btnToRead, ReadingStatus.TO_READ);
        setupHover(btnReading, ReadingStatus.READING);
        setupHover(btnRead, ReadingStatus.READ);

        btnToRead.setOnAction(e -> onFilterClick.accept(ReadingStatus.TO_READ));
        btnReading.setOnAction(e -> onFilterClick.accept(ReadingStatus.READING));
        btnRead.setOnAction(e -> onFilterClick.accept(ReadingStatus.READ));

        filterBox.getChildren().addAll(btnToRead, btnReading, btnRead);

        headerBox.getChildren().addAll(profileBox, statsBox, filterBox);

        booksGrid = new FlowPane();
        booksGrid.setHgap(30);
        booksGrid.setVgap(30);
        booksGrid.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(booksGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + BG_COLOR + ";");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("transparent-pane");

        root.getChildren().addAll(navbar, sep, headerBox, scrollPane);

        return root;
    }

    private void setButtonState(Button btn, boolean active) {
        btn.getStyleClass().removeAll(STYLE_INACTIVE, STYLE_ACTIVE);
        btn.getStyleClass().add(active ? STYLE_ACTIVE : STYLE_INACTIVE);
    }

    private void setupHover(Button btn, ReadingStatus status) {
        btn.setOnMouseEntered(e -> {
            if (currentActiveStatus != status) {
                setButtonState(btn, true);
            }
        });
        btn.setOnMouseExited(e -> {
            if (currentActiveStatus != status) {
                setButtonState(btn, false);
            }
        });
    }

    private void resetButtonColors() {
        setButtonState(btnToRead, false);
        setButtonState(btnReading, false);
        setButtonState(btnRead, false);
    }

    public void setActiveButton(ReadingStatus status) {
        this.currentActiveStatus = status;
        resetButtonColors();
        if (status == null) return;

        if (status == ReadingStatus.TO_READ) {
            setButtonState(btnToRead, true);
        } else if (status == ReadingStatus.READING) {
            setButtonState(btnReading, true);
        } else if (status == ReadingStatus.READ) {
            setButtonState(btnRead, true);
        }
    }

    public void populateGrid(java.util.List<javafx.scene.layout.VBox> bookCards) {
        booksGrid.getChildren().clear();
        if (bookCards.isEmpty()) {
            Label emptyLabel = new Label("Nessun libro trovato in questa sezione.");
            emptyLabel.getStyleClass().add("empty-state-label");
            booksGrid.getChildren().add(emptyLabel);
        } else {
            booksGrid.getChildren().addAll(bookCards);
        }
    }

    public void showChooseSectionPrompt() {
        booksGrid.getChildren().clear();
        Label promptLabel = new Label("Scegli quale sezione visualizzare.");
        promptLabel.getStyleClass().add("empty-state-label");
        booksGrid.getChildren().add(promptLabel);
    }

    public VBox buildBookCard(BookBean book, ReadingStatus currentStatus,
                              Consumer<ReadingStatus> onStatusChange, Runnable onRemove,
                              IntConsumer onRate, Runnable onClick) {
        return super.buildBookCard(
                null,
                book,
                currentStatus != null ? currentStatus.name() : null,
                rawStatus -> handleStatusChange(rawStatus, onStatusChange, onRemove),
                onRate,
                onClick,
                true
        );
    }

    private void handleStatusChange(String rawStatus, Consumer<ReadingStatus> onStatusChange, Runnable onRemove) {
        if (rawStatus == null || rawStatus.equals("RIMUOVI") || rawStatus.equals("Rimuovi libro")) {
            onRemove.run();
            return;
        }

        ReadingStatus status = null;
        if (rawStatus.equals(ReadingStatus.TO_READ.name()) || rawStatus.equals(ReadingStatus.TO_READ.getDisplayName())) {
            status = ReadingStatus.TO_READ;
        } else if (rawStatus.equals(ReadingStatus.READING.name()) || rawStatus.equals(ReadingStatus.READING.getDisplayName())) {
            status = ReadingStatus.READING;
        } else if (rawStatus.equals(ReadingStatus.READ.name()) || rawStatus.equals(ReadingStatus.READ.getDisplayName())) {
            status = ReadingStatus.READ;
        }

        if (status != null) onStatusChange.accept(status);
    }
}