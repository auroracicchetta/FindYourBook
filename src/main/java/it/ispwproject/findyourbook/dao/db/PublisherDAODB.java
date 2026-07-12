package it.ispwproject.findyourbook.dao.db;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.dao.ConnectionFactory;
import it.ispwproject.findyourbook.dao.PublisherDAO;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.model.Publisher;
import it.ispwproject.findyourbook.model.PublisherStats;
import it.ispwproject.findyourbook.util.logger.AppLogger;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PublisherDAODB implements PublisherDAO {


    @Override
    public Publisher findById(int id) throws DAOException {

        String query = "SELECT id, nome, cognome, username, email, password, data_registrazione, descrizione " +
                "FROM utenti WHERE id = ? AND ruolo = 'PUBLISHER'";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDate regDate = rs.getDate("data_registrazione") != null ? rs.getDate("data_registrazione").toLocalDate() : LocalDate.now(java.time.ZoneId.systemDefault());

                    return new Publisher(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password"),
                            regDate,
                            rs.getString("descrizione")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel recupero della casa editrice: " + e.getMessage(), e);
        }
        return null;
    }


    @Override
    public void publishBook(BookBean book, String publisherUsername) throws DAOException {
        String query = "INSERT INTO published_books (title, author, genre, description, image_url, publisher_username) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getGenre());
            stmt.setString(4, book.getDescription());
            stmt.setString(5, book.getImageUrl());
            stmt.setString(6, publisherUsername);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                throw new DAOException("Nessuna riga inserita nel database durante la pubblicazione.");
            }

        } catch (SQLException e) {
            AppLogger.logError("Errore SQL durante la pubblicazione del libro: " + e.getMessage());
            throw new DAOException("Errore di comunicazione col database durante la pubblicazione.");
        }
    }

    @Override
    public List<Book> getCatalogByPublisher(String publisherUsername) throws DAOException {
        List<Book> books = new ArrayList<>();

        String query = "SELECT id, title, author, genre, description, image_url FROM published_books WHERE publisher_username = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, publisherUsername);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book b = new Book();
                    b.setId(rs.getInt("id"));

                    b.setTitle(rs.getString("title"));
                    b.setAuthor(rs.getString("author"));
                    b.setGenre(rs.getString("genre"));
                    b.setDescription(rs.getString("description"));
                    b.setImageUrl(rs.getString("image_url"));

                    b.setPublisherUsername(publisherUsername);

                    books.add(b);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore lettura catalogo: " + e.getMessage());
        }
        return books;
    }

    @Override
    public void updateBook(BookBean book, String publisherUsername) throws DAOException {

        String query = "UPDATE published_books SET description = ?, image_url = ? WHERE title = ? AND publisher_username = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, book.getDescription());
            stmt.setString(2, book.getImageUrl());
            stmt.setString(3, book.getTitle());
            stmt.setString(4, publisherUsername);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Nessun libro trovato con questo titolo per l'aggiornamento.");
            }
            AppLogger.logInfo("Libro aggiornato con successo: " + book.getTitle());

        } catch (SQLException e) {
            AppLogger.logError("Errore SQL durante l'aggiornamento: " + e.getMessage());
            throw new DAOException("Errore di comunicazione col database durante l'aggiornamento.");
        }
    }

    @Override
    public void deleteBook(String bookTitle, String publisherUsername) throws DAOException {
        String query = "DELETE FROM published_books WHERE title = ? AND publisher_username = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, bookTitle);
            stmt.setString(2, publisherUsername);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Nessun libro trovato con questo titolo da eliminare.");
            }
            AppLogger.logInfo("Libro rimosso dal database: " + bookTitle);

        } catch (SQLException e) {
            AppLogger.logError("Errore SQL durante l'eliminazione: " + e.getMessage());
            throw new DAOException("Errore di comunicazione col database durante l'eliminazione.");
        }
    }

    @Override
    public PublisherStats getPublisherStatistics(String publisherUsername) throws DAOException {
        int totalBooks = 0;
        int totalSales = 0;
        java.util.Map<String, Integer> topSelling = new java.util.LinkedHashMap<>();
        java.util.Map<String, Integer> byGenre = new java.util.LinkedHashMap<>();

        String queryTotals = "SELECT COUNT(id) AS total_books, COALESCE(SUM(copie_vendute), 0) AS total_sales FROM published_books WHERE publisher_username = ?";
        String queryTop = "SELECT title, copie_vendute FROM published_books WHERE publisher_username = ? ORDER BY copie_vendute DESC LIMIT 4";
        String queryGenre = "SELECT genre, COALESCE(SUM(copie_vendute), 0) AS genre_sales FROM published_books WHERE publisher_username = ? GROUP BY genre";

        try (Connection conn = ConnectionFactory.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(queryTotals)) {
                ps.setString(1, publisherUsername);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalBooks = rs.getInt("total_books");
                        totalSales = rs.getInt("total_sales");
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(queryTop)) {
                ps.setString(1, publisherUsername);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        topSelling.put(rs.getString("title"), rs.getInt("copie_vendute"));
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(queryGenre)) {
                ps.setString(1, publisherUsername);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        byGenre.put(rs.getString("genre"), rs.getInt("genre_sales"));
                    }
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore nel recupero statistiche dal DB: " + e.getMessage());
        }

        return new PublisherStats(totalBooks, totalSales, topSelling, byGenre);
    }

}