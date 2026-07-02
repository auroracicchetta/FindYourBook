package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.service.BookService;
import it.ispwproject.findyourbook.dao.db.BookDAODB;      // Suggerimento: rinomina in BookDAODB
import it.ispwproject.findyourbook.dao.db.FavoritesDAODB; // Suggerimento: rinomina in FavoritesDAODB
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.dao.DAOFactory;// Suggerimento: rinomina in Book
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.service.GoogleBooksService;
import it.ispwproject.findyourbook.util.logger.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class BookController {

    private final BookService bookService = new BookService();
    private final BookDAODB bookDao = new BookDAODB();
    private final FavoritesDAODB favoritesDao = new FavoritesDAODB();

    public List<BookBean> getBooksByGenre(String genre) {
        List<BookBean> allResults = new ArrayList<>();
        try {
            // 1. Prendiamo l'array dei 3 autori famosi per questo genere
            String[] famosiAutori = getAuthorsForGenre(genre);
            GoogleBooksService googleService = new GoogleBooksService();

            // 2. IL TUO CICLO FOR! Scarica i libri di ogni autore e li unisce
            for (String autore : famosiAutori) {
                AppLogger.logInfo("🔮 [Google Books] Scarico bestseller di: " + autore);
                List<BookBean> libriAutore = googleService.searchBooks("inauthor:\"" + autore + "\"");
                allResults.addAll(libriAutore);
            }

            if (allResults.isEmpty()) {
                throw new Exception("Nessun libro trovato per questo genere.");
            }

            // 3. Sincronizziamo i voti e gli stati con il Database
            new it.ispwproject.findyourbook.controller.applicativo.UserLibraryController().syncBooksWithDatabase(allResults);

            AppLogger.logInfo("✅ Totale libri caricati nella vetrina: " + allResults.size());
            return allResults;

        } catch (Exception e) {
            AppLogger.logWarning("⚠️ Ricerca generi fallita: " + e.getMessage() + " -> Fallback su DB...");
            return getBooksFromDb(genre);
        }
    }

    // Il dizionario con i 3 autori per genere (BookTok + Classici)
    private String[] getAuthorsForGenre(String genre) {
        return switch (genre.toLowerCase()) {
            case "classici" -> new String[]{"Jane Austen", "Charles Dickens", "Oscar Wilde"};
            case "fantasy" -> new String[]{"Sarah J. Maas", "Rebecca Yarros", "J.R.R. Tolkien"};
            case "romance" -> new String[]{"Ali Hazelwood", "Emily Henry", "Colleen Hoover"};
            case "gialli" -> new String[]{"Agatha Christie", "Arthur Conan Doyle", "Stephen King"};
            case "avventura" -> new String[]{"Jules Verne", "Alexandre Dumas", "Jack London"};
            case "poesia" -> new String[]{"Emily Dickinson", "Charles Baudelaire", "Alda Merini"};
            case "storici" -> new String[]{"Ken Follett", "Madeline Miller", "Valerio Massimo Manfredi"};
            case "filosofici" -> new String[]{"Seneca", "Platone", "Friedrich Nietzsche"};
            default -> new String[]{genre}; // Se non trova il genere, cerca il nome scritto
        };
    }

    private List<BookBean> getBooksFromDb(String genre) {
        List<BookBean> results = new ArrayList<>();
        try {
            List<Book> dbBooks = bookDao.findByGenere(genre);
            for (Book b : dbBooks) {
                results.add(new BookBean(b.getTitolo(), b.getAutore(), b.getGenere(), b.getImmagineUrl()));
            }
            AppLogger.logInfo("✅ Data loaded from local Database.");
        } catch (Exception e) {
            AppLogger.logError("❌ Critical Error: DB not responding! " + e.getMessage());
        }
        return results;
    }

    public List<BookBean> getFavoriteBooks(String username, String readingStatus) {
        List<BookBean> results = new ArrayList<>();

        try {
            // 1. Chiamiamo il DAO usando la Stringa 'username' invece dell'int 'userId'
            // 2. Il nuovo DAO ci restituisce già direttamente i BookBean pronti per la GUI!
            results = DAOFactory.getFavoritesDAO().getLibriByStato(username, readingStatus);

            AppLogger.logInfo("✅ Caricati " + results.size() + " libri nello stato: " + readingStatus);

        } catch (Exception e) {
            AppLogger.logError("❌ Errore nel recupero dei libri preferiti: " + e.getMessage());
        }

        return results;
    }

    // --- NUOVO METODO PER LA RICERCA DALLA BARRA IN ALTO ---
    public List<BookBean> searchBooks(String query) {
        List<BookBean> results = new ArrayList<>();
        try {
            AppLogger.logInfo("🔍 [Ricerca Libera] Ricerca in corso per: " + query);
            GoogleBooksService googleService = new GoogleBooksService();

            // 1. Scarica i libri da Google
            results = googleService.searchBooks(query);

            if (!results.isEmpty()) {
                // 2. LA MAGIA: Sincronizziamo i risultati con il Database/Memoria prima di mostrarli!
                new it.ispwproject.findyourbook.controller.applicativo.UserLibraryController().syncBooksWithDatabase(results);
            }

        } catch (Exception e) {
            AppLogger.logError("❌ Errore durante la ricerca libera: " + e.getMessage());
        }
        return results;
    }

    public void updateBookRating(BookBean book, int rating) {
        try {
            String username = SessionManager.getInstance().getLoggedUser().getUsername();
            // Richiama il DAO che hai già scritto!
            DAOFactory.getFavoritesDAO().updateValutazione(username, book.getTitle(), rating);
            AppLogger.logInfo("✅ Valutazione salvata!");
        } catch (Exception e) {
            AppLogger.logError("❌ Errore durante l'aggiornamento della valutazione: " + e.getMessage());
        }
    }

}