package it.ispwproject.findyourbook.view.cli;

public class EditProfileView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("FindYourBook  –  Profilo");
    }


    public void mostraMenu() {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Modifica email");
        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraDatiAttuali(String nome, String email) {
        CLIRenderer.sezione("Profilo attuale");
        CLIRenderer.campo("Nome/Azienda", nome);
        CLIRenderer.campo("Email", email);
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa("Scelta");
    }

    public String chiediCampo(String label) {
        return CLIRenderer.chiediCampo(label);
    }

    public void mostraSuccesso(String messaggio) {
        CLIRenderer.successo(messaggio);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }
}