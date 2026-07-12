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
                goNext(context, this);
                return;
            }

            userLibraryController.syncBooksWithDatabase(results);

            view.showResults(results);
            int choice = view.askBookChoice(results.size());

            if (choice == 0) {
                goNext(context, this);
                return;
            }

            if (choice < 1 || choice > results.size()) {
                view.showMessage("Scelta non valida.");
                goNext(context, this);
                return;
            }

            BookBean selectedBook = results.get(choice - 1);

            manageBook(selectedBook, context);

        } catch (Exception e) {
            view.showMessage("Errore durante la ricerca: " + e.getMessage());
            goNext(context, this);
        }
    }

    private void manageBook(BookBean book, CLIStateMachine context) {
        boolean back = false;
        while (!back) {
            view.showBookDetails(book);
            String action = view.askAction();

            switch (action) {
                case "1" -> {
                    String statusStr = view.askStatus();
                    if (statusStr != null) {
                        try {
                            if (statusStr.equals("RIMUOVI")) {
                                userLibraryController.removeBookFromLibrary(book);
                                view.showMessage("Libro rimosso dalla tua libreria.");
                                book.setStatus(null);
                            } else {
                                ReadingStatus newStatus = ReadingStatus.valueOf(statusStr);
                                userLibraryController.saveBookToLibrary(book, newStatus);
                                view.showMessage("Stato aggiornato con successo a: " + newStatus);
                                book.setStatus(newStatus);
                            }
                        } catch (Exception e) {
                            view.showMessage("Errore nell'aggiornamento: " + e.getMessage());
                        }
                    } else {
                        view.showMessage("Stato non valido.");
                    }
                }
                case "2" -> {
                    int rating = view.askRating();
                    if (rating >= 1 && rating <= 5) {
                        try {
                            userLibraryController.rateBook(book, rating);
                            view.showMessage("Voto inserito con successo!");
                        } catch (Exception e) {
                            view.showMessage("Errore nell'inserimento del voto: " + e.getMessage());
                        }
                    } else {
                        view.showMessage("Voto non valido. Deve essere compreso tra 1 e 5.");
                    }
                }
                case "0" -> back = true;
                default -> view.showMessage("Azione non riconosciuta.");
            }
        }

        goNext(context, this);
    }
}