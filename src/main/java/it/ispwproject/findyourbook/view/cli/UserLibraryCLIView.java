package it.ispwproject.findyourbook.view.cli;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import java.util.List;

public class UserLibraryCLIView {

    public void showHeader(String username) {
        CLIRenderer.intestazione("LIBRERIA DI " + username.toUpperCase());
    }

    public ReadingStatus askStatusFilter() {
        CLIRenderer.sezione("Quali libri vuoi visualizzare?");
        CLIRenderer.voceMenu(1, ReadingStatus.TO_READ.getDisplayName());
        CLIRenderer.voceMenu(2, ReadingStatus.READING.getDisplayName());
        CLIRenderer.voceMenu(3, ReadingStatus.READ.getDisplayName());
        CLIRenderer.voceMenuZero("Torna indietro");

        String choice = CLIRenderer.chiediSceltaStringa("Scelta");
        return switch (choice) {
            case "1" -> ReadingStatus.TO_READ;
            case "2" -> ReadingStatus.READING;
            case "3" -> ReadingStatus.READ;
            default -> null; // 0 o input non valido
        };
    }

    public void showBooksList(List<BookBean> books, ReadingStatus status) {
        CLIRenderer.sezione("I tuoi libri: " + status.getDisplayName());
        if (books.isEmpty()) {
            CLIRenderer.messaggio("Nessun libro in questa categoria.");
            return;
        }

        CLIRenderer.vuota();
        for (int i = 0; i < books.size(); i++) {
            BookBean b = books.get(i);
            String rating = (b.getRating() > 0) ? b.getRating() + " " + CLIRenderer.STAR : "Non votato";
            CLIRenderer.voceMenu((i + 1), b.getTitle() + " di " + b.getAuthor() + " | Voto: " + rating);
        }
    }

    public int askBookChoice(int max) {
        // Usa la protezione di CLIRenderer per accettare solo numeri tra 0 e il massimo
        return CLIRenderer.chiediScelta("Seleziona il numero del libro per gestirlo (0 per tornare ai filtri)", 0, max);
    }

    public void showBookDetails(BookBean book) {
        CLIRenderer.sezione("GESTIONE: " + book.getTitle());
        CLIRenderer.campo("Stato attuale", book.getStatus() != null ? book.getStatus().getDisplayName() : "Nessuno");
        CLIRenderer.campo("Voto", book.getRating() > 0 ? book.getRating() + " " + CLIRenderer.STAR : "Non votato");
    }

    public String askAction() {
        CLIRenderer.vuota();
        CLIRenderer.voceMenu(1, "Cambia Stato (Sposta in un'altra lista)");
        CLIRenderer.voceMenu(2, "Lascia un Voto (1-5)");
        CLIRenderer.voceMenu(3, "Rimuovi dalla libreria");
        CLIRenderer.voceMenuZero("Indietro");

        return CLIRenderer.chiediSceltaStringa("Azione");
    }

    public ReadingStatus askNewStatus() {
        CLIRenderer.sezione("Nuovo stato");
        CLIRenderer.voceMenu(1, "Da Leggere");
        CLIRenderer.voceMenu(2, "In Lettura");
        CLIRenderer.voceMenu(3, "Letto");

        String choice = CLIRenderer.chiediSceltaStringa("Scelta");
        return switch (choice) {
            case "1" -> ReadingStatus.TO_READ;
            case "2" -> ReadingStatus.READING;
            case "3" -> ReadingStatus.READ;
            default -> null;
        };
    }

    public int askRating() {
        return CLIRenderer.chiediScelta("Inserisci un voto", 1, 5);
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