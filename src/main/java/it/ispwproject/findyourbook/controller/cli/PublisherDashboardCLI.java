package it.ispwproject.findyourbook.controller.cli;

import it.ispwproject.findyourbook.pattern.state.CLIStateMachine;
import it.ispwproject.findyourbook.view.cli.PublisherDashboardCLIView;

public class PublisherDashboardCLI extends DashboardCLI {

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
                logout();
                redirect(context, new InitialCLI());
            }
            default  -> {
                view.showError("Scelta non valida.");
                repeat(context);
            }
        }
    }
}