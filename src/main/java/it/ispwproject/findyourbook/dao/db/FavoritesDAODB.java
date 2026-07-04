package it.ispwproject.findyourbook.dao.db;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.dao.ConnectionFactory;
import it.ispwproject.findyourbook.dao.FavoritesDAO;
import it.ispwproject.findyourbook.exception.DAOException;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class FavoritesDAODB implements FavoritesDAO {

    @Override
    public void addLibroPreferito(String username, BookBean book, String statoLettura) throws DAOException {
        // CORREZIONE: Inserita la colonna 'descrizione' per salvare la trama permanentemente
        String query = "INSERT INTO preferiti (username, titolo, autore, immagine_url, stato_lettura, descrizione) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE stato_lettura = ?, descrizione = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getImageUrl());
            stmt.setString(5, statoLettura);

            // Salviamo la trama in modo sicuro
            String desc = book.getDescription() != null ? book.getDescription() : "Trama non disponibile.";
            stmt.setString(6, desc);

            stmt.setString(7, statoLettura);
            stmt.setString(8, desc);

            stmt.executeUpdate();

        } catch (Exception e) {
            throw new DAOException("Errore nell'aggiunta del libro ai preferiti: " + e.getMessage());
        }
    }

    @Override
    public void removeLibroPreferito(String username, String titoloLibro) throws DAOException {
        String query = "DELETE FROM preferiti WHERE username = ? AND titolo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, titoloLibro);
            stmt.executeUpdate();
        } catch (Exception e) { throw new DAOException(e.getMessage()); }
    }

    @Override
    public void updateValutazione(String username, String titoloLibro, int rating) throws DAOException {
        String query = "UPDATE preferiti SET valutazione = ? WHERE username = ? AND titolo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, rating);
            stmt.setString(2, username);
            stmt.setString(3, titoloLibro);
            stmt.executeUpdate();
        } catch (Exception e) { throw new DAOException(e.getMessage()); }
    }

    @Override
    public List<BookBean> getLibriByStato(String username, String statoLettura) throws DAOException {
        List<BookBean> lista = new ArrayList<>();
        // CORREZIONE: Aggiunta la colonna 'descrizione' alla query di caricamento
        String query = "SELECT titolo, autore, immagine_url, valutazione, descrizione FROM preferiti WHERE username = ? AND stato_lettura = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, statoLettura);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Usiamo il costruttore completo (il primo in BookBean) per agganciare subito la trama
                    BookBean book = new BookBean(
                            rs.getString("titolo"),
                            rs.getString("autore"),
                            "",
                            rs.getString("immagine_url"),
                            rs.getString("descrizione")
                    );
                    book.setRating(rs.getInt("valutazione"));

                    // CRITICO: Assegniamo esplicitamente lo stato al Bean per non rompere la UI!
                    book.setStatus(statoLettura);

                    lista.add(book);
                }
            }
        } catch (Exception e) {
            throw new DAOException("Errore durante il recupero dei libri: " + e.getMessage());
        }

        return lista;
    }
}