package it.ispwproject.findyourbook.view.cli;

import it.ispwproject.findyourbook.bean.BookBean;
import java.util.List;

public class SearchBooksCLIView {

    public void showHeader() {
        CLIRenderer.intestazione("RICERCA LIBRI");
    }

    public String askSearchQuery() {
        return CLIRenderer.chiediSceltaStringa("Cosa vuoi cercare? (0 per tornare indietro)");
    }

    public void showResults(List<BookBean> results) {
        CLIRenderer.sezione("Risultati trovati: " + results.size());

        for (int i = 0; i < results.size(); i++) {
            BookBean b = results.get(i);
            String status = (b.getStatus() != null) ? "[" + b.getStatus().getDisplayName() + "]" : "[Nessuno stato]";
            String rating = (b.getRating() > 0) ? b.getRating() + " ★" : "Non votato";

            CLIRenderer.messaggio((i + 1) + ". " + b.getTitle() + " - " + b.getAuthor() + " " + status + " - " + rating);
        }
    }

    public int askBookChoice(int max) {
        return CLIRenderer.chiediScelta("Seleziona il numero del libro per gestirlo (0 per cercare di nuovo)", 0, max);
    }

    public void showBookDetails(BookBean book) {
        CLIRenderer.sezione("DETTAGLIO LIBRO");
        CLIRenderer.campo("Titolo", book.getTitle());
        CLIRenderer.campo("Autore", book.getAuthor());
        CLIRenderer.campo("Genere", book.getGenre());
        CLIRenderer.campo("Trama", book.getDescription());

        String status = book.getStatus() != null ? book.getStatus().getDisplayName() : "Nessuno";
        CLIRenderer.campo("Stato attuale", status);
        CLIRenderer.campo("Voto", book.getRating() > 0 ? String.valueOf(book.getRating()) : "Non votato");
        CLIRenderer.separatore();
    }

    public String askAction() {
        CLIRenderer.sezione("Scegli un'azione");
        CLIRenderer.voceMenu(1, "Imposta/Cambia Stato (Da leggere, In lettura, Letto)");
        CLIRenderer.voceMenu(2, "Lascia un Voto (1-5)");
        CLIRenderer.voceMenuZero("Torna ai risultati");
        return CLIRenderer.chiediSceltaStringa("Scelta");
    }

    public String askStatus() {
        CLIRenderer.sezione("Scegli il nuovo stato");
        CLIRenderer.voceMenu(1, "Da leggere");
        CLIRenderer.voceMenu(2, "In lettura");
        CLIRenderer.voceMenu(3, "Letto");
        CLIRenderer.voceMenuZero("Annulla");

        int choice = CLIRenderer.chiediScelta("Scelta", 0, 3);
        return switch (choice) {
            case 1 -> "TO_READ";
            case 2 -> "READING";
            case 3 -> "READ";
            default -> null;
        };
    }

    public int askRating() {
        return CLIRenderer.chiediScelta("Inserisci un voto (0 per annullare)", 0, 5);
    }

    public void showMessage(String msg) {
        CLIRenderer.successo(msg);
    }

    public void showError(String msg) {
        CLIRenderer.errore(msg);
    }
}