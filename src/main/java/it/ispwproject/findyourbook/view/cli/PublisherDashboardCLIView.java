package it.ispwproject.findyourbook.view.cli;

public class PublisherDashboardCLIView {

    public void showDashboardMenu() {
        CLIRenderer.intestazione("DASHBOARD CASA EDITRICE");
        CLIRenderer.sezione("Menu Principale");

        CLIRenderer.voceMenu(1, "Gestione Catalogo");
        CLIRenderer.voceMenu(2, "Pubblica un nuovo libro");
        CLIRenderer.voceMenu(3, "Statistiche di vendita");
        CLIRenderer.voceMenu(4, "Il mio Profilo (Modifica Email)");

        CLIRenderer.voceMenuZero("Logout");
        CLIRenderer.separatore();
    }

    public String askChoice() {
        return CLIRenderer.chiediSceltaStringa("Scegli un'opzione");
    }

    public void showError(String msg) {
        CLIRenderer.errore(msg);
    }
}