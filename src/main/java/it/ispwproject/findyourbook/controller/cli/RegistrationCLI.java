package it.ispwproject.findyourbook.controller.cli;

import it.ispwproject.findyourbook.bean.RegistrationBean;
import it.ispwproject.findyourbook.controller.applicativo.RegistrationController;
import it.ispwproject.findyourbook.pattern.state.AbstractCLIState;
import it.ispwproject.findyourbook.pattern.state.CLIStateMachine;
import it.ispwproject.findyourbook.view.cli.RegistrationCLIView;
import it.ispwproject.findyourbook.enumerator.Role;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public class RegistrationCLI extends AbstractCLIState {

    private final RegistrationController registrationController = new RegistrationController();
    private final RegistrationCLIView view = new RegistrationCLIView();

    @Override
    public void entry(CLIStateMachine context) {
        view.showHeader();
        view.showMessaggio("(Digita '0' in qualsiasi campo per annullare e tornare indietro)");
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            RegistrationBean bean = new RegistrationBean();

            String nome = view.askField("Nome");
            if (isBackChoice(nome)) { goNext(context, new LoginCLI()); return; }
            bean.setName(nome);

            String cognome = view.askField("Cognome");
            if (isBackChoice(cognome)) { goNext(context, new LoginCLI()); return; }
            bean.setSurname(cognome);

            String username = view.askField("Username");
            if (isBackChoice(username)) { goNext(context, new LoginCLI()); return; }
            bean.setUsername(username);

            String email = view.askField("Email");
            if (isBackChoice(email)) { goNext(context, new LoginCLI()); return; }
            bean.setEmail(email);

            String password = view.askPasswordField("Password");
            if (isBackChoice(password)) { goNext(context, new LoginCLI()); return; }
            bean.setPassword(password);

            String confirm = view.askPasswordField("Conferma password");
            if (isBackChoice(confirm)) { goNext(context, new LoginCLI()); return; }
            bean.setConfirmPassword(confirm);

            Role role = view.askRole();
            bean.setRole(role);

            if (role == Role.READER) {
                String dataString = view.askField("Data di nascita (formato: AAAA-MM-GG)");
                if (isBackChoice(dataString)) { goNext(context, new LoginCLI()); return; }

                if (!impostaDataDiNascita(bean, dataString, context)) {
                    return;
                }
            } else {
                String desc = view.askField("Descrizione attività");
                if (isBackChoice(desc)) { goNext(context, new LoginCLI()); return; }
                bean.setDescription(desc);
            }

            registrationController.register(bean);

            view.showSuccess("Registrazione completata!");
            goNext(context, new LoginCLI());

        } catch (Exception e) {
            view.showError("Errore: " + e.getMessage());
            goNext(context, this);
        }
    }

    private boolean impostaDataDiNascita(RegistrationBean bean, String dataString, CLIStateMachine context) {
        try {
            bean.setBirthDate(LocalDate.parse(dataString));
            return true;
        } catch (DateTimeParseException e) {
            view.showError("Formato data non valido.");
            goNext(context, this);
            return false;
        }
    }
}