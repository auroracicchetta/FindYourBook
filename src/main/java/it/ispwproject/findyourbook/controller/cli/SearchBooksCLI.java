package it.ispwproject.findyourbook.controller.cli;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.controller.applicativo.BookController;
import it.ispwproject.findyourbook.controller.applicativo.UserLibraryController;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import it.ispwproject.findyourbook.pattern.state.AbstractCLIState;
import it.ispwproject.findyourbook.pattern.state.CLIStateMachine;
import it.ispwproject.findyourbook.view.cli.SearchBooksCLIView;

import java.util.List;

public class SearchBooksCLI extends AbstractCLIState {

    private final SearchBooksCLIView view = new SearchBooksCLIView();

    private final BookController bookController = new BookController();
    private final UserLibraryController userLibraryController = new UserLibraryController();

    @Override
    public void entry(CLIStateMachine context) {
        view.showHeader();
    }

    @Override
    public void action(CLIStateMachine context) {
        String query = view.askSearchQuery();

        if (isBackChoice(query)) {
            goBack(context);
            return;
        }

        try {

            List<BookBean> results = bookController.searchBooks(query);

            if (results.isEmpty()) {
                view.showMessage("Nessun libro trovato per '" + query + "'.");
                repeat(context);
                return;
            }

            userLibraryController.syncBooksWithDatabase(results);

            boolean newSearch = false;
            while (!newSearch) {
                view.showResults(results);
                int choice = view.askBookChoice(results.size());

                if (choice == 0) {
                    newSearch = true;
                } else if (choice < 1 || choice > results.size()) {
                    view.showMessage("Scelta non valida.");
                } else {
                    BookBean selectedBook = results.get(choice - 1);
                    manageBook(selectedBook);
                }
            }

            repeat(context);

        } catch (Exception e) {
            view.showMessage("Errore durante la ricerca: " + e.getMessage());
            repeat(context);
        }
    }

    private void manageBook(BookBean book) {
        boolean back = false;
        while (!back) {
            view.showBookDetails(book);
            String action = view.askAction();

            switch (action) {
                case "1" -> handleBookAction(book);
                case "2" -> {
                    int rating = view.askRating();
                    if (rating == 0) {
                        view.showMessage("Operazione annullata.");
                    } else {
                        try {
                            userLibraryController.rateBook(book, rating);
                            book.setRating(rating);
                            view.showMessage("Voto inserito con successo!");
                        } catch (Exception e) {
                            view.showMessage("Errore nell'inserimento del voto: " + e.getMessage());
                        }
                    }
                }
                case "0" -> back = true;
                default -> view.showMessage("Azione non riconosciuta.");
            }
        }
    }

    private void handleBookAction(BookBean book) {
        String statusStr = view.askStatus();
        if (statusStr != null) {
            try {
                ReadingStatus newStatus = ReadingStatus.valueOf(statusStr);
                userLibraryController.saveBookToLibrary(book, newStatus);
                view.showMessage("Stato aggiornato con successo a: " + newStatus);
                book.setStatus(newStatus);
            } catch (Exception e) {
                view.showMessage("Errore nell'aggiornamento: " + e.getMessage());
            }
        } else {
            view.showMessage("Operazione annullata.");
        }
    }
}