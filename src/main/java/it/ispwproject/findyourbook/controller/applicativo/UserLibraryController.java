package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.dao.DAOFactory;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.model.User;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public class UserLibraryController {

    // --- COSTANTI PER GLI STATI DEI LIBRI ---
    private static final String STATUS_LETTO = "LETTO";
    private static final String STATUS_DA_LEGGERE = "DA_LEGGERE";
    private static final String STATUS_IN_LETTURA = "IN_LETTURA";
    private static final String STATUS_RIMUOVI = "RIMUOVI";

    // --- METODO 1: SALVA O AGGIORNA UN LIBRO ---
    public void saveBookToLibrary(BookBean book, String status) {
        User currentUser = SessionManager.getInstance().getLoggedUser();

        if (currentUser == null) {
            AppLogger.logWarning("⚠️ Nessun utente loggato! Impossibile salvare il libro.");
            return;
        }

        try {
            // 1. Salviamo il libro e il suo stato
            DAOFactory.getFavoritesDAO().addLibroPreferito(currentUser.getUsername(), book, status);

            // 2. LA MAGIA: Se l'utente aveva già cliccato le stelline sulla card prima di scegliere lo stato,
            // ci assicuriamo che vengano salvate nel database subito dopo l'inserimento!
            if (book.getRating() > 0) {
                DAOFactory.getFavoritesDAO().updateValutazione(currentUser.getUsername(), book.getTitle(), book.getRating());
            }

            AppLogger.logInfo("✅ [APP CONTROLLER] Salvato il libro: '" + book.getTitle() + "' nello stato: " + status);
        } catch (Exception e) {
            AppLogger.logError("❌ Errore durante il salvataggio: " + e.getMessage());
        }
    }

    // --- METODO 2: RIMUOVE UN LIBRO DALLA LIBRERIA ---
    public void removeBookFromLibrary(BookBean book) {
        User currentUser = SessionManager.getInstance().getLoggedUser();

        if (currentUser == null) {
            AppLogger.logWarning("⚠️ Nessun utente loggato! Impossibile rimuovere il libro.");
            return;
        }

        try {
            // Chiamiamo il metodo di rimozione dal DAO
            DAOFactory.getFavoritesDAO().removeLibroPreferito(currentUser.getUsername(), book.getTitle());

            AppLogger.logInfo("🗑️ [APP CONTROLLER] Rimosso il libro: '" + book.getTitle() +
                    "' per l'utente '" + currentUser.getUsername() + "'");
        } catch (Exception e) {
            AppLogger.logError("❌ Errore durante la rimozione: " + e.getMessage());
        }
    }

    public void rateBook(BookBean book, int rating) {
        User currentUser = SessionManager.getInstance().getLoggedUser();
        if (currentUser == null) return;

        try {
            // 1. LA MAGIA: Se valuti un libro che non è ancora salvato, lo inseriamo automaticamente in "Letto"!
            if (book.getStatus() == null || book.getStatus().trim().isEmpty() || book.getStatus().equals(STATUS_RIMUOVI)) {
                book.setStatus(STATUS_LETTO);
                DAOFactory.getFavoritesDAO().addLibroPreferito(currentUser.getUsername(), book, STATUS_LETTO);
                AppLogger.logInfo("📖 Libro aggiunto automaticamente a 'Letto' perché è stato valutato.");
            }

            // 2. Ora che siamo certi che il libro esiste nel DB, salviamo le stelline!
            DAOFactory.getFavoritesDAO().updateValutazione(currentUser.getUsername(), book.getTitle(), rating);
            AppLogger.logInfo("⭐ Voto salvato: " + rating + " stelle per '" + book.getTitle() + "'");

        } catch (Exception e) {
            AppLogger.logError("Errore durante il salvataggio del voto: " + e.getMessage());
        }
    }

    // --- METODO MAGICO: Sincronizza i libri di Google col tuo Database ---
    public void syncBooksWithDatabase(java.util.List<BookBean> searchResults) {
        User currentUser = SessionManager.getInstance().getLoggedUser();
        if (currentUser == null) return;

        try {
            // Scarichiamo le tue tre liste dal Database
            java.util.List<BookBean> daLeggere = DAOFactory.getFavoritesDAO().getLibriByStato(currentUser.getUsername(), STATUS_DA_LEGGERE);
            java.util.List<BookBean> inLettura = DAOFactory.getFavoritesDAO().getLibriByStato(currentUser.getUsername(), STATUS_IN_LETTURA);
            java.util.List<BookBean> letti = DAOFactory.getFavoritesDAO().getLibriByStato(currentUser.getUsername(), STATUS_LETTO);

            for (BookBean googleBook : searchResults) {
                checkAndSync(googleBook, daLeggere, STATUS_DA_LEGGERE);
                checkAndSync(googleBook, inLettura, STATUS_IN_LETTURA);
                checkAndSync(googleBook, letti, STATUS_LETTO);
            }
        } catch (Exception e) {
            AppLogger.logError("Errore durante la sincronizzazione: " + e.getMessage());
        }
    }

    // Sincronizzazione "Intelligente" a prova di sottotitoli di Google!
    private void checkAndSync(BookBean googleBook, java.util.List<BookBean> dbBooks, String status) {
        String gTitle = googleBook.getTitle().toLowerCase().trim();

        for (BookBean dbBook : dbBooks) {
            String dbTitle = dbBook.getTitle().toLowerCase().trim();

            // SPIA DIAGNOSTICA: Stampiamo cosa sta confrontando il programma!
            AppLogger.logInfo("🔍 Sincronizzazione: Confronto Google ['" + gTitle + "'] con DB ['" + dbTitle + "'] -> Voto DB: " + dbBook.getRating());

            if (gTitle.contains(dbTitle) || dbTitle.contains(gTitle)) {
                googleBook.setRating(dbBook.getRating());
                googleBook.setStatus(status);
                AppLogger.logInfo("✅ MATCH TROVATO! Copiate " + dbBook.getRating() + " stelle su: " + googleBook.getTitle());
                break;
            }
        }
    }
}