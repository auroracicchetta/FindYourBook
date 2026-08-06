package it.ispwproject.findyourbook.controller.cli;

import it.ispwproject.findyourbook.pattern.state.AbstractCLIState;
import it.ispwproject.findyourbook.pattern.state.CLIStateMachine;
import it.ispwproject.findyourbook.view.cli.InitialView;

public class InitialCLI extends AbstractCLIState {

    private final InitialView view = new InitialView();

    @Override
    public void entry(CLIStateMachine context) {
        view.showWelcome();
    }

    @Override
    public void action(CLIStateMachine context) {
        view.showMenu();
        switch (view.askChoice()) {
            case "1" -> goNext(context, new LoginCLI());
            case "2" -> goNext(context, new RegistrationCLI());
            case "0" -> context.setState(null);
            default  -> {
                view.showError("Scelta non valida.");
                repeat(context);
            }
        }
    }
}