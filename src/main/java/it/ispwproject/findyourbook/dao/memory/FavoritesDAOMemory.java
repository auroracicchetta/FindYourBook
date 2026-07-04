package it.ispwproject.findyourbook.dao.memory;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.dao.FavoritesDAO;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.util.logger.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class FavoritesDAOMemory implements FavoritesDAO {

    // 1. LA VERA CASSAFORTE: Salviamo stringhe e numeri, NON l'oggetto BookBean intero!
    private static class MemoryRecord {
        String username;
        String title;
        String author;
        String genre;
        String imageUrl;
        String status;
        int rating;
        String description; // <--- CORREZIONE: AGGIUNTO IL CAMPO PER SALVARE LA TRAMA

        MemoryRecord(String u, BookBean b, String s) {
            this.username = u;
            this.title = b.getTitle();
            this.author = b.getAuthor();
            this.genre = b.getGenre();
            this.imageUrl = b.getImageUrl();
            this.status = s;
            this.rating = b.getRating();

            // Salviamo la trama in cassaforte, gestendo il caso in cui sia vuota
            this.description = b.getDescription() != null ? b.getDescription() : "Trama non disponibile.";
        }
    }

    private static final List<MemoryRecord> DB_FINTO = new ArrayList<>();

    @Override
    public void addLibroPreferito(String username, BookBean book, String statoLettura) throws DAOException {
        AppLogger.logInfo("💾 [MEMORY DAO] Richiesta salvataggio per: '" + book.getTitle() + "' in stato: " + statoLettura);

        for (MemoryRecord item : DB_FINTO) {
            if (item.username.equals(username) && item.title.trim().equalsIgnoreCase(book.getTitle().trim())) {
                item.status = statoLettura;

                // Se Google ci passa una trama nuova, la aggiorniamo
                if (book.getDescription() != null) {
                    item.description = book.getDescription();
                }

                AppLogger.logInfo("   -> Libro già presente. Aggiornato stato a: " + statoLettura);
                return;
            }
        }
        DB_FINTO.add(new MemoryRecord(username, book, statoLettura));
        AppLogger.logInfo("   -> Libro NUOVO aggiunto alla cassaforte!");
    }

    @Override
    public void removeLibroPreferito(String username, String titoloLibro) throws DAOException {
        DB_FINTO.removeIf(item -> item.username.equals(username) && item.title.trim().equalsIgnoreCase(titoloLibro.trim()));
    }

    @Override
    public void updateValutazione(String username, String titoloLibro, int rating) throws DAOException {
        AppLogger.logInfo("💾 [MEMORY DAO] Aggiorno stelline per '" + titoloLibro + "' a " + rating);

        for (MemoryRecord item : DB_FINTO) {
            if (item.username.equals(username) && item.title.trim().equalsIgnoreCase(titoloLibro.trim())) {
                item.rating = rating;
                AppLogger.logInfo("   -> ✅ Voto bloccato in cassaforte con successo!");
                return;
            }
        }
        AppLogger.logWarning("   -> ⚠️ ATTENZIONE: Libro non trovato in cassaforte! (Non dovrebbe succedere)");
    }

    @Override
    public List<BookBean> getLibriByStato(String username, String statoLettura) throws DAOException {
        List<BookBean> risultati = new ArrayList<>();

        for (MemoryRecord item : DB_FINTO) {
            if (item.username.equals(username) && item.status.equals(statoLettura)) {
                // CORREZIONE: Ricreiamo il BookBean usando il costruttore completo che accetta la trama!
                BookBean b = new BookBean(item.title, item.author, item.genre, item.imageUrl, item.description);
                b.setRating(item.rating);
                b.setStatus(item.status);
                risultati.add(b);
            }
        }
        return risultati;
    }
}