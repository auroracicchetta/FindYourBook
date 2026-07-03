package it.ispwproject.findyourbook.dao.db;

import it.ispwproject.findyourbook.dao.ConnectionFactory;
import it.ispwproject.findyourbook.dao.BookDAO;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.exception.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAODB implements BookDAO {
    // Risolto il code smell del SELECT * inserendo le colonne esatte
    private static final String SELECT_BY_GENRE = "SELECT id, titolo, autore, genere, immagine_url FROM libro WHERE genere = ?";

    @Override
    public List<Book> findByGenere(String genere) throws DAOException {
        List<Book> libri = new ArrayList<>();

        // Il try-with-resources apre la connessione e la chiude automaticamente
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_GENRE)) {

            ps.setString(1, genere);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                libri.add(mapToLibro(rs));
            }
        } catch (Exception e) { // Usiamo Exception per catturare sia SQLException che errori di connessione
            throw new DAOException("Errore nel caricamento: " + e.getMessage(), e);
        }
        return libri;
    }

    private Book mapToLibro(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("id"),
                rs.getString("titolo"),
                rs.getString("autore"),
                rs.getString("genere"),
                rs.getString("immagine_url")
        );
    }

    @Override
    public void save(Book book) throws DAOException {
        // ... logica di salvataggio
    }
}