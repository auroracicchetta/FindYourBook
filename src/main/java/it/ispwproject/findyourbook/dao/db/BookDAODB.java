package it.ispwproject.findyourbook.dao.db;

import it.ispwproject.findyourbook.dao.ConnectionFactory;
import it.ispwproject.findyourbook.dao.BookDAO;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.exception.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAODB implements BookDAO {

    // 1. CORREZIONE: Usiamo la tabella 'published_books' e le colonne in inglese!
    private static final String SELECT_BY_GENRE = "SELECT id, title, author, genre, image_url, description, publisher_username, copie_vendute FROM published_books WHERE genre = ?";
    private static final String SEARCH_QUERY = "SELECT id, title, author, genre, image_url, description, publisher_username, copie_vendute FROM published_books WHERE title LIKE ? OR author LIKE ?";

    @Override
    public List<Book> findByGenre(String genre) throws DAOException {
        List<Book> books = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_GENRE)) {

            ps.setString(1, genre);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapToBook(rs));
                }
            }
        } catch (Exception e) {
            throw new DAOException("Errore nel caricamento dei libri per genere: " + e.getMessage(), e);
        }
        return books;
    }

    @Override
    public List<Book> searchByQuery(String query) throws DAOException {
        List<Book> books = new ArrayList<>();
        String likePattern = "%" + query + "%";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SEARCH_QUERY)) {

            ps.setString(1, likePattern);
            ps.setString(2, likePattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapToBook(rs));
                }
            }
        } catch (Exception e) {
            throw new DAOException("Errore nella ricerca libri: " + e.getMessage(), e);
        }
        return books;
    }

    private Book mapToBook(ResultSet rs) throws SQLException {
        Book book = new Book();

        // 2. CORREZIONE: Estraiamo i dati usando i nomi esatti delle colonne del DB
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setGenre(rs.getString("genre"));
        book.setImageUrl(rs.getString("image_url"));
        book.setDescription(rs.getString("description"));
        book.setPublisherUsername(rs.getString("publisher_username"));
        book.setCopieVendute(rs.getInt("copie_vendute"));

        return book;
    }

    @Override
    public void save(Book book) throws DAOException {
        // Logica di salvataggio (se prevista in futuro per i publisher)
    }
}