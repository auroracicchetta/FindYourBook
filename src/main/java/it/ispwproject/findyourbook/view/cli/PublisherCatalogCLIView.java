package it.ispwproject.findyourbook.view.cli;

import it.ispwproject.findyourbook.bean.BookBean;
import java.util.List;

public class PublisherCatalogCLIView {

    public void showHeader() {
        CLIRenderer.intestazione("IL TUO CATALOGO");
    }

    public void showCatalog(List<BookBean> catalog) {
        if (catalog.isEmpty()) {
            CLIRenderer.messaggio("Il tuo catalogo è attualmente vuoto.");
            return;
        }

        CLIRenderer.vuota();
        for (int i = 0; i < catalog.size(); i++) {
            // Usa il renderer per stampare [1] Titolo - Genere
            CLIRenderer.voceMenu((i + 1), catalog.get(i).getTitle() + " - " + catalog.get(i).getGenre());
        }
    }

    public int askBookChoice(int max) {
        // Usa il controllo numerico protetto del CLIRenderer
        return CLIRenderer.chiediScelta("Seleziona un libro per gestirlo (0 per tornare alla dashboard)", 0, max);
    }

    public void showBookDetails(BookBean book) {
        CLIRenderer.sezione("DETTAGLI LIBRO");
        CLIRenderer.campo("Titolo", book.getTitle());
        CLIRenderer.campo("Trama", book.getDescription());
        CLIRenderer.campo("Copertina", book.getImageUrl());
    }

    public String askAction() {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Modifica Dati (Trama/Copertina)");
        CLIRenderer.voceMenu(2, "Elimina dal catalogo");
        CLIRenderer.voceMenuZero("Torna al catalogo");

        return CLIRenderer.chiediSceltaStringa("Azione");
    }

    public String askField(String prompt, String currentValue) {
        CLIRenderer.messaggio("Premi INVIO per mantenere il valore attuale: " + currentValue);
        String input = CLIRenderer.chiediCampo(prompt);
        return input.isEmpty() ? currentValue : input;
    }

    public void showMessage(String msg) {
        CLIRenderer.messaggio(msg);
    }

    public void showSuccess(String msg) {
        CLIRenderer.successo(msg);
    }

    public void showError(String msg) {
        CLIRenderer.errore(msg);
    }
}