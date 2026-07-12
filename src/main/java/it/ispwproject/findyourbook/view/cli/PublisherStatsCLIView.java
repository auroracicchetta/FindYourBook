package it.ispwproject.findyourbook.view.cli;

import it.ispwproject.findyourbook.bean.PublisherStatsBean;

public class PublisherStatsCLIView {

    public void showHeader() {
        CLIRenderer.intestazione("REPORT AZIENDALE");
    }

    public void showStats(PublisherStatsBean stats) {
        CLIRenderer.campo("Libri a Catalogo", String.valueOf(stats.getTotalBooksPublished()));
        CLIRenderer.campo("Totale Libri Letti", String.valueOf(stats.getTotalCopiesSold()));

        CLIRenderer.sezione("I Libri più Letti dagli Utenti");
        if (stats.getTopSellingBooks().isEmpty()) {
            CLIRenderer.messaggio("Nessun dato disponibile.");
        } else {
            stats.getTopSellingBooks().forEach((titolo, letture) ->
                    CLIRenderer.messaggio(CLIRenderer.BULLET + " " + titolo + ": " + letture + " letture"));
        }
    }

    public void waitUser() {
        CLIRenderer.vuota();
        CLIRenderer.chiediScelta("Digita 0 per tornare alla dashboard", 0, 0);
    }

    public void showError(String msg) {
        CLIRenderer.errore(msg);
    }
}