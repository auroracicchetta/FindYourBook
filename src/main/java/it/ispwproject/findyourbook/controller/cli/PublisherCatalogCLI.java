package it.ispwproject.findyourbook.controller.cli;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.controller.applicativo.PublisherController;
import it.ispwproject.findyourbook.pattern.state.AbstractCLIState;
import it.ispwproject.findyourbook.pattern.state.CLIStateMachine;
import it.ispwproject.findyourbook.view.cli.PublisherCatalogCLIView;

import java.util.List;

public class PublisherCatalogCLI extends AbstractCLIState {

    private final PublisherCatalogCLIView view = new PublisherCatalogCLIView();
    private final PublisherController publisherController = new PublisherController();

    @Override
    public void entry(CLIStateMachine context) {
        view.showHeader();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            List<BookBean> catalog = publisherController.getMyCatalog();
            view.showCatalog(catalog);

            if (catalog.isEmpty()) {
                view.showMessage("Premi INVIO per tornare indietro.");
                new java.util.Scanner(System.in).nextLine();
                goBack(context);
                return;
            }

            int choice = view.askBookChoice(catalog.size());

            if (choice == 0) {
                goNext(context, new PublisherDashboardCLI());
                return;
            } else if (choice > 0) {
                BookBean selectedBook = catalog.get(choice - 1);
                manageBook(selectedBook, context);
            } else {
                view.showMessage("Scelta non valida.");
                goNext(context, this); // Ricarica il catalogo
            }

        } catch (Exception e) {
            view.showMessage("Errore nel caricamento del catalogo: " + e.getMessage());
            goBack(context);
        }
    }

    private void manageBook(BookBean book, CLIStateMachine context) {
        boolean back = false;
        while (!back) {
            view.showBookDetails(book);
            String action = view.askAction();

            switch (action) {
                case "1" -> {
                    String newDesc = view.askField("Nuova Trama", book.getDescription());
                    String newUrl = view.askField("Nuovo URL Copertina", book.getImageUrl());

                    book.setDescription(newDesc);
                    book.setImageUrl(newUrl);

                    try {
                        publisherController.updateExistingBook(book);
                        view.showMessage("Libro aggiornato con successo!");
                    } catch (Exception e) {
                        view.showMessage("Errore durante l'aggiornamento: " + e.getMessage());
                    }
                }
                case "2" -> {
                    try {
                        publisherController.removeBookFromCatalog(book.getTitle());
                        view.showMessage("Libro rimosso dal catalogo.");
                        back = true;
                    } catch (Exception e) {
                        view.showMessage("Errore durante l'eliminazione: " + e.getMessage());
                    }
                }
                case "0" -> back = true;
                default -> view.showMessage("Azione non riconosciuta.");
            }
        }

        goNext(context, this);
    }
}