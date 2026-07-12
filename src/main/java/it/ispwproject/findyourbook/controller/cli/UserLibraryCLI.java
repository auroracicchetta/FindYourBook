package it.ispwproject.findyourbook.controller.cli;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.controller.applicativo.BookController;
import it.ispwproject.findyourbook.controller.applicativo.UserLibraryController;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.pattern.state.AbstractCLIState;
import it.ispwproject.findyourbook.pattern.state.CLIStateMachine;
import it.ispwproject.findyourbook.view.cli.UserLibraryCLIView;

import java.util.List;

public class UserLibraryCLI extends AbstractCLIState {

    private final UserLibraryCLIView view = new UserLibraryCLIView();
    private final BookController bookController = new BookController();
    private final UserLibraryController userLibraryController = new UserLibraryController();

    @Override
    public void entry(CLIStateMachine context) {
        String username = SessionManager.getInstance().getLoggedUser().getUsername();
        userLibraryController.checkInactiveReading();
        view.showHeader(username);
    }

    @Override
    public void action(CLIStateMachine context) {
        ReadingStatus selectedStatus = view.askStatusFilter();

        if (selectedStatus == null) {
            goNext(context, new ReaderDashboardCLI());
            return;
        }

        try {
            String username = SessionManager.getInstance().getLoggedUser().getUsername();
            List<BookBean> userBooks = bookController.getFavoriteBooks(username, selectedStatus);

            boolean backToFilters = false;
            while (!backToFilters) {
                view.showBooksList(userBooks, selectedStatus);

                if (userBooks.isEmpty()) {
                    break;
                }

                int choice = view.askBookChoice(userBooks.size());

                if (choice == 0) {
                    backToFilters = true;
                } else if (choice > 0) {
                    BookBean selectedBook = userBooks.get(choice - 1);
                    manageBook(selectedBook, userBooks);
                } else {
                    view.showMessage("Scelta non valida.");
                }
            }

            goNext(context, this);

        } catch (Exception e) {
            view.showMessage("Errore nel recupero della libreria: " + e.getMessage());
            goNext(context, this);
        }
    }

    private void manageBook(BookBean book, List<BookBean> currentList) {
        boolean back = false;
        while (!back) {
            view.showBookDetails(book);
            String action = view.askAction();

            switch (action) {
                case "1" -> back = handleBookAction(book, currentList);
                case "2" -> {
                    int rating = view.askRating();
                    if (rating >= 1 && rating <= 5) {
                        try {
                            userLibraryController.rateBook(book, rating);
                            view.showMessage("Voto inserito con successo!");
                        } catch (Exception e) {
                            view.showMessage("Errore: " + e.getMessage());
                        }
                    } else {
                        view.showMessage("Voto non valido. Deve essere compreso tra 1 e 5.");
                    }
                }
                case "3" -> {
                    try {
                        userLibraryController.removeBookFromLibrary(book);
                        view.showMessage("Libro rimosso dalla libreria.");
                        currentList.remove(book);
                        back = true;
                    } catch (Exception e) {
                        view.showMessage("Errore durante la rimozione: " + e.getMessage());
                    }
                }
                case "0" -> back = true;
                default -> view.showMessage("Azione non riconosciuta.");
            }
        }
    }

    private boolean handleBookAction(BookBean book, List<BookBean> currentList) {
        ReadingStatus newStatus = view.askNewStatus();
        if (newStatus != null) {
            try {
                userLibraryController.saveBookToLibrary(book, newStatus);
                book.setStatus(newStatus);
                view.showMessage("Stato aggiornato a " + newStatus.getDisplayName());
                currentList.remove(book);
                return true;
            } catch (Exception e) {
                view.showMessage("Errore aggiornamento: " + e.getMessage());
            }
        } else {
            view.showMessage("Stato non valido.");
        }
        return false;
    }
}