package it.ispwproject.findyourbook.controller.cli;

import it.ispwproject.findyourbook.controller.applicativo.LoginController;
import it.ispwproject.findyourbook.pattern.state.AbstractCLIState;
import it.ispwproject.findyourbook.pattern.state.CLIStateMachine;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.view.cli.LoginView;


public class LoginCLI extends AbstractCLIState {

    private final LoginController loginController = new LoginController();
    private final LoginView view = new LoginView();

    @Override
    public void action(CLIStateMachine context) {
        String[] credentials = view.askCredentials();
        String username = credentials[0];
        String password = credentials[1];

        if (isBackChoice(username)) {
            goBack(context);
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            view.showInputError();
            goNext(context, this); // Riprova
            return;
        }

        try {
            LoginController.LoginResult result = loginController.login(username, password);

            String name = SessionManager.getInstance().getLoggedUser().getUsername();
            view.showSuccess(name);

            switch (result) {
                case SUCCESSO_READER ->
                        goNext(context, new ReaderDashboardCLI());
                case SUCCESSO_PUBLISHER ->
                        goNext(context, new PublisherDashboardCLI());
            }

        } catch (Exception e) {
            view.showError("Errore durante il login: " + e.getMessage());
            goNext(context, this); // Riprova
        }
    }
}