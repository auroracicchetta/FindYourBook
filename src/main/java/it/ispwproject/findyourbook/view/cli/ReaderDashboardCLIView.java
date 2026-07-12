package it.ispwproject.findyourbook.view.cli;

public class ReaderDashboardCLIView {

    public void showDashboardMenu(String username) {
        CLIRenderer.intestazioneBenvenuto(username, "Lettore");
        CLIRenderer.sezione("Menu Principale");

        CLIRenderer.voceMenu(1, "Cerca Libri (Titolo/Autore)");
        CLIRenderer.voceMenu(2, "Esplora per Genere");
        CLIRenderer.voceMenu(3, "I Miei Libri");
        CLIRenderer.voceMenu(4, "Il mio Profilo (Modifica Email)");

        CLIRenderer.voceMenuZero("Logout");
        CLIRenderer.separatore();
    }

    public String askChoice() {
        return CLIRenderer.chiediSceltaStringa("Scelta");
    }

    public void showError(String msg) {
        CLIRenderer.errore(msg);
    }
}

