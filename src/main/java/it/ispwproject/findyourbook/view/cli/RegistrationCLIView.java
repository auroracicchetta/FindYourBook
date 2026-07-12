package it.ispwproject.findyourbook.view.cli;

import it.ispwproject.findyourbook.enumerator.Role;

public class RegistrationCLIView {

    public void showHeader() {
        CLIRenderer.intestazione("FindYourBook – Registrazione");
    }

    public String askField(String label) {
        return CLIRenderer.chiediCampo(label);
    }

    public String askPasswordField(String label) {
        return CLIRenderer.chiediCampo(label);
    }

    public Role askRole() {
        CLIRenderer.messaggio("Scegli il tuo ruolo:");
        CLIRenderer.voceMenu(1, "Lettore");
        CLIRenderer.voceMenu(2, "Casa Editrice");
        CLIRenderer.voceMenuZero("Annulla e Torna Indietro");
        String choice = CLIRenderer.chiediSceltaStringa("Ruolo");
        if (choice.equals("0")) return null;
        return choice.equals("2") ? Role.PUBLISHER : Role.READER;
    }

    public void showMessaggio(String message) {
        CLIRenderer.messaggio(message);
    }

    public void showSuccess(String message) {
        CLIRenderer.successo(message);
    }

    public void showError(String message) {
        CLIRenderer.errore(message);
    }
}