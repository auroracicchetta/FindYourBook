package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.dao.DAOFactory;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.model.User;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public class UserLibraryController {

    private static final String STATUS_LETTO = "LETTO";
    private static final String STATUS_DA_LEGGERE = "DA_LEGGERE";
    private static final String STATUS_IN_LETTURA = "IN_LETTURA";
    private static final String STATUS_RIMUOVI = "RIMUOVI";

    public void saveBookToLibrary(BookBean book, String status) {
        User currentUser = SessionManager.getInstance().getLoggedUser();
        if (currentUser == null) return;

        try {
            DAOFactory.getFavoritesDAO().addLibroPreferito(currentUser.getUsername(), book, status);
            if (book.getRating() > 0) {
                DAOFactory.getFavoritesDAO().updateValutazione(currentUser.getUsername(), book.getTitle(), book.getRating());
            }
            AppLogger.logInfo("✅ Salvato il libro: '" + book.getTitle() + "' nello stato: " + status);
        } catch (Exception e) {
            AppLogger.logError("❌ Errore durante il salvataggio: " + e.getMessage());
        }
    }

    public void removeBookFromLibrary(BookBean book) {
        User currentUser = SessionManager.getInstance().getLoggedUser();
        if (currentUser == null) return;

        try {
            DAOFactory.getFavoritesDAO().removeLibroPreferito(currentUser.getUsername(), book.getTitle());
            AppLogger.logInfo("🗑️ Rimosso il libro: '" + book.getTitle() + "'");
        } catch (Exception e) {
            AppLogger.logError("❌ Errore durante la rimozione: " + e.getMessage());
        }
    }

    public void rateBook(BookBean book, int rating) {
        User currentUser = SessionManager.getInstance().getLoggedUser();
        if (currentUser == null) return;

        try {
            if (book.getStatus() == null || book.getStatus().trim().isEmpty() || book.getStatus().equals(STATUS_RIMUOVI)) {
                book.setStatus(STATUS_LETTO);
                DAOFactory.getFavoritesDAO().addLibroPreferito(currentUser.getUsername(), book, STATUS_LETTO);
            }
            DAOFactory.getFavoritesDAO().updateValutazione(currentUser.getUsername(), book.getTitle(), rating);
            AppLogger.logInfo("⭐ Voto salvato: " + rating + " stelle per '" + book.getTitle() + "'");
        } catch (Exception e) {
            AppLogger.logError("❌ Errore durante il salvataggio del voto: " + e.getMessage());
        }
    }

    // --- SINCRONIZZAZIONE BLINDATA ---
    public void syncBooksWithDatabase(java.util.List<BookBean> searchResults) {
        try {
            User currentUser = SessionManager.getInstance().getLoggedUser();
            if (currentUser == null) return;

            java.util.List<BookBean> daLeggere = DAOFactory.getFavoritesDAO().getLibriByStato(currentUser.getUsername(), STATUS_DA_LEGGERE);
            java.util.List<BookBean> inLettura = DAOFactory.getFavoritesDAO().getLibriByStato(currentUser.getUsername(), STATUS_IN_LETTURA);
            java.util.List<BookBean> letti = DAOFactory.getFavoritesDAO().getLibriByStato(currentUser.getUsername(), STATUS_LETTO);

            for (BookBean googleBook : searchResults) {
                // Se Google restituisce un libro rotto, saltiamo al prossimo senza far crashare tutto
                if (googleBook == null || googleBook.getTitle() == null) continue;

                checkAndSync(googleBook, daLeggere, STATUS_DA_LEGGERE);
                checkAndSync(googleBook, inLettura, STATUS_IN_LETTURA);
                checkAndSync(googleBook, letti, STATUS_LETTO);
            }
        } catch (Exception e) {
            AppLogger.logError("❌ Errore critico durante la sincronizzazione: " + e.getMessage());
        }
    }

    private void checkAndSync(BookBean googleBook, java.util.List<BookBean> dbBooks, String status) {
        // Se la lista dei libri salvati nel DB è vuota, resettiamo i campi del libro
        if (dbBooks == null || dbBooks.isEmpty()) {
            googleBook.setRating(0);
            googleBook.setStatus(null);
            return;
        }

        String gTitle = googleBook.getTitle().toLowerCase().trim();
        boolean trovato = false; // Flag per tracciare se abbiamo trovato il libro

        for (BookBean dbBook : dbBooks) {
            if (dbBook != null && dbBook.getTitle() != null) {
                String dbTitle = dbBook.getTitle().toLowerCase().trim();

                if (gTitle.equals(dbTitle) || gTitle.contains(dbTitle) || dbTitle.contains(gTitle)) {
                    googleBook.setRating(dbBook.getRating());
                    googleBook.setStatus(status);

                    if (dbBook.getDescription() != null && !dbBook.getDescription().trim().isEmpty()) {
                        googleBook.setDescription(dbBook.getDescription());
                    }

                    AppLogger.logInfo("✅ Sincronizzato con successo: " + googleBook.getTitle());
                    trovato = true;
                    break;
                }
            }
        }

        // Se dopo aver controllato TUTTA la lista non l'abbiamo trovato,
        // resettiamo i dati del libro (cancella il "fantasma")
        if (!trovato) {
            googleBook.setRating(0);
            googleBook.setStatus(null);
        }
    }
}