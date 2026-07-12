package it.ispwproject.findyourbook.controller.cli;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.controller.applicativo.BookController;
import it.ispwproject.findyourbook.controller.applicativo.UserLibraryController;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import it.ispwproject.findyourbook.pattern.state.AbstractCLIState;
import it.ispwproject.findyourbook.pattern.state.CLIStateMachine;
import it.ispwproject.findyourbook.view.cli.SearchBooksCLIView;
import it.ispwproject.findyourbook.view.cli.SearchByGenreCLIView;

import java.util.List;

public class SearchByGenreCLI extends AbstractCLIState {

    private final SearchByGenreCLIView genreView = new SearchByGenreCLIView();

    private final SearchBooksCLIView resultsView = new SearchBooksCLIView();

    private final BookController bookController = new BookController();
    private final UserLibraryController userLibraryController = new UserLibraryController();

    @Override
    public void entry(CLIStateMachine context) {
        genreView.showHeader();
    }

    @Override
    public void action(CLIStateMachine context) {
        String genere = genreView.askGenre();

        if (genere.equals("0")) {
            goBack(context);
            return;
        }

        try {

            List<BookBean> results = bookController.getBooksByGenre(genere);

            if (results.isEmpty()) {
                resultsView.showMessage("Nessun libro trovato per il genere '" + genere + "'.");
                goBack(context);
                return;
            }

            userLibraryController.syncBooksWithDatabase(results);

            resultsView.showResults(results);
            int choice = resultsView.askBookChoice(results.size());

            if (choice == 0) {
                goBack(context);
                return;
            }

            BookBean selectedBook = results.get(choice - 1);
            manageBook(selectedBook, context);

        } catch (Exception e) {
            resultsView.showError("Errore durante la ricerca per genere: " + e.getMessage());
            goNext(context, this);
        }
    }

    private void manageBook(BookBean book, CLIStateMachine context) {
        boolean back = false;
        while (!back) {
            resultsView.showBookDetails(book);
            String action = resultsView.askAction();

            switch (action) {
                case "1" -> {
                    String statusStr = resultsView.askStatus();
                    if (statusStr != null) {
                        try {
                            if (statusStr.equals("RIMUOVI")) {
                                userLibraryController.removeBookFromLibrary(book);
                                resultsView.showMessage("Libro rimosso dalla tua libreria.");
                                book.setStatus(null);
                            } else {
                                ReadingStatus newStatus = ReadingStatus.valueOf(statusStr);
                                userLibraryController.saveBookToLibrary(book, newStatus);
                                resultsView.showMessage("Stato aggiornato con successo a: " + newStatus.getDisplayName());
                                book.setStatus(newStatus);
                            }
                        } catch (Exception e) {
                            resultsView.showError("Errore nell'aggiornamento: " + e.getMessage());
                        }
                    }
                }
                case "2" -> {
                    int rating = resultsView.askRating();
                    try {
                        userLibraryController.rateBook(book, rating);
                        resultsView.showMessage("Voto inserito con successo!");
                        book.setRating(rating);
                    } catch (Exception e) {
                        resultsView.showError("Errore nell'inserimento del voto: " + e.getMessage());
                    }
                }
                case "0" -> back = true;
                default -> resultsView.showError("Azione non riconosciuta.");
            }
        }

        goNext(context, this);
    }
}