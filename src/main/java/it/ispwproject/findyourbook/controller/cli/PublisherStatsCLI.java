package it.ispwproject.findyourbook.controller.cli;

import it.ispwproject.findyourbook.bean.PublisherStatsBean;
import it.ispwproject.findyourbook.controller.applicativo.PublisherStatsController;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.pattern.state.AbstractCLIState;
import it.ispwproject.findyourbook.pattern.state.CLIStateMachine;
import it.ispwproject.findyourbook.view.cli.PublisherStatsCLIView;

public class PublisherStatsCLI extends AbstractCLIState {

    private final PublisherStatsCLIView view = new PublisherStatsCLIView();
    private final PublisherStatsController controller = new PublisherStatsController();

    @Override
    public void entry(CLIStateMachine context) {
        view.showHeader();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            String username = SessionManager.getInstance().getLoggedUser().getUsername();
            PublisherStatsBean stats = controller.getStatistics(username);

            view.showStats(stats);
            view.waitUser();

        } catch (Exception e) {
            view.showError("Errore nel caricamento delle statistiche: " + e.getMessage());
            view.waitUser();
        }

        goBack(context);
    }
}