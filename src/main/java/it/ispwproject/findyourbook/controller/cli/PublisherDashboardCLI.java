package it.ispwproject.findyourbook.controller.cli;

import it.ispwproject.findyourbook.dao.ConnectionFactory;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.pattern.state.AbstractCLIState;
import it.ispwproject.findyourbook.pattern.state.CLIStateMachine;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import it.ispwproject.findyourbook.view.cli.PublisherDashboardCLIView;

import java.sql.SQLException;

public class PublisherDashboardCLI extends AbstractCLIState {

    private final PublisherDashboardCLIView view = new PublisherDashboardCLIView();

    @Override
    public void action(CLIStateMachine context) {
        view.showDashboardMenu();
        String choice = view.askChoice();

        switch (choice) {
            case "1" -> goNext(context, new PublisherCatalogCLI());
            case "2" -> goNext(context, new PublishBookCLI());
            case "3" -> goNext(context, new PublisherStatsCLI());
            case "4" -> goNext(context, new EditProfileCLI());
            case "0" -> {
                SessionManager.getInstance().clearSession();
                try {
                    ConnectionFactory.clearRole();
                } catch (SQLException e) {
                    AppLogger.logError("Errore durante il reset delle credenziali DB al logout: " + e.getMessage());
                }
                redirect(context, new InitialCLI());
            }
            default  -> {
                view.showError("Scelta non valida.");
                repeat(context);
            }
        }
    }
}