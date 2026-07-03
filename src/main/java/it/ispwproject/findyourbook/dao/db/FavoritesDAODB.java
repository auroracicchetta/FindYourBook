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
        // Salviamo tutti i dettagli del libro. La chiave primaria/unica dovrebbe essere (username, titolo)
        String query = "INSERT INTO preferiti (username, titolo, autore, immagine_url, stato_lettura) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE stato_lettura = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getImageUrl());
            stmt.setString(5, statoLettura);
            stmt.setString(6, statoLettura); // Se esiste già, aggiorna solo lo stato!

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
        // Risolto il code smell specificando le colonne esatte al posto di SELECT *
        String query = "SELECT titolo, autore, immagine_url, valutazione FROM preferiti WHERE username = ? AND stato_lettura = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, statoLettura);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BookBean book = new BookBean(
                            rs.getString("titolo"),
                            rs.getString("autore"),
                            "",
                            rs.getString("immagine_url")

                    );
                    book.setRating(rs.getInt("valutazione"));
                    lista.add(book);
                }
            }
        } catch (Exception e) {
            throw new DAOException("Errore durante il recupero dei libri: " + e.getMessage());
        }

        return lista; // <-- Questa deve stare FUORI dal blocco try-catch
    }
}