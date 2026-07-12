package it.ispwproject.findyourbook.controller.cli;

import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.pattern.state.AbstractCLIState;
import it.ispwproject.findyourbook.pattern.state.CLIStateMachine;
import it.ispwproject.findyourbook.view.cli.ReaderDashboardCLIView;

public class ReaderDashboardCLI extends AbstractCLIState {

    private final ReaderDashboardCLIView view = new ReaderDashboardCLIView();

    @Override
    public void action(CLIStateMachine context) {

        String username = SessionManager.getInstance().getLoggedUser().getUsername();

        view.showDashboardMenu(username);
        String choice = view.askChoice();

        switch (choice) {
            case "1" -> goNext(context, new SearchBooksCLI());
            case "2" -> goNext(context, new SearchByGenreCLI());
            case "3" -> goNext(context, new UserLibraryCLI());
            case "4" -> goNext(context, new EditProfileCLI());
            case "0" -> {

                SessionManager.getInstance().clearSession();
                context.setState(null);
            }
            default  -> {
                view.showError("Scelta non valida.");
                goNext(context, this);
            }
        }
    }
}