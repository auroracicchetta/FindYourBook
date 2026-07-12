package it.ispwproject.findyourbook.controller.cli;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.controller.applicativo.PublisherController;
import it.ispwproject.findyourbook.pattern.state.AbstractCLIState;
import it.ispwproject.findyourbook.pattern.state.CLIStateMachine;
import it.ispwproject.findyourbook.view.cli.PublishBookCLIView;

public class PublishBookCLI extends AbstractCLIState {

    private final PublishBookCLIView view = new PublishBookCLIView();
    private final PublisherController publisherController = new PublisherController();

    @Override
    public void entry(CLIStateMachine context) {
        view.showHeader();
    }

    @Override
    public void action(CLIStateMachine context) {
        String title = view.askField("Titolo");
        if (isBackChoice(title)) { goBack(context); return; }

        String author = view.askField("Autore");
        if (isBackChoice(author)) { goBack(context); return; }

        String genre = view.askField("Genere (es. gialli, romance, storici)");
        if (isBackChoice(genre)) { goBack(context); return; }

        String description = view.askField("Trama/Descrizione");
        if (isBackChoice(description)) { goBack(context); return; }

        String coverUrl = view.askField("URL Copertina (opzionale)");
        if (isBackChoice(coverUrl)) { goBack(context); return; }

        if (title.isEmpty() || author.isEmpty() || genre.isEmpty()) {
            view.showMessage("Errore: Titolo, Autore e Genere sono obbligatori.");
            goNext(context, this);
            return;
        }

        BookBean newBook = new BookBean();
        newBook.setTitle(title);
        newBook.setAuthor(author);
        newBook.setGenre(genre.toLowerCase());
        newBook.setDescription(description);
        newBook.setImageUrl(coverUrl);

        try {
            publisherController.publishNewBook(newBook);
            view.showMessage("Successo! Il libro '" + title + "' è stato pubblicato nel tuo catalogo.");
            goBack(context); // Torna alla dashboard
        } catch (Exception e) {
            view.showMessage("Errore durante la pubblicazione: " + e.getMessage());
            goNext(context, this);
        }
    }
}