package it.ispwproject.findyourbook.view.cli;

public class LoginView {

    public String[] askCredentials() {
        CLIRenderer.intestazione("FindYourBook – Accedi");
        CLIRenderer.voceMenuZero(" (o digita '0' per tornare al menu iniziale)");
        CLIRenderer.vuota();
        String username = CLIRenderer.chiediCampo("Username");
        if (username.equals("0")) return new String[]{"0", "0"};

        String password = CLIRenderer.chiediCampo("Password");
        return new String[]{username, password};
    }

    public void showInputError() {
        CLIRenderer.errore("Inserisci sia username che password.");
    }

    public void showError(String message) {
        CLIRenderer.errore(message);
    }

    public void showSuccess(String username) {
        CLIRenderer.messaggio(CLIRenderer.OK + " Benvenuto, " + username + "!");
    }
}