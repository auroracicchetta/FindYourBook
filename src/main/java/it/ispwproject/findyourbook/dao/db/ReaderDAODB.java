package it.ispwproject.findyourbook.dao.db;

import it.ispwproject.findyourbook.dao.ConnectionFactory;
import it.ispwproject.findyourbook.dao.ReaderDAO;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.model.Reader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReaderDAODB implements ReaderDAO {

    @Override
    public Reader findById(int id) throws DAOException {
        // CORREZIONE: 'LETTORE' diventa 'READER'
        String query = "SELECT id, nome, cognome, username, email, password, data_registrazione, data_nascita " +
                "FROM utenti WHERE id = ? AND ruolo = 'READER'";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LocalDate regDate = rs.getDate("data_registrazione") != null ? rs.getDate("data_registrazione").toLocalDate() : LocalDate.now();
                    LocalDate birthDate = rs.getDate("data_nascita") != null ? rs.getDate("data_nascita").toLocalDate() : null;

                    return new Reader(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password"),
                            regDate,
                            birthDate
                    );
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore nel recupero del lettore: " + e.getMessage(), e);
        }
        return null;
    }


    @Override
    public void addFavoriteBook(String username, Book book, String readingStatus) throws DAOException {
        // Aggiungiamo data_inizio_lettura alla query
        String query = "INSERT INTO preferiti (username, titolo, autore, immagine_url, stato_lettura, descrizione, data_inizio_lettura) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE stato_lettura = ?, descrizione = ?, data_inizio_lettura = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getImageUrl());
            stmt.setString(5, readingStatus);

            String desc = book.getDescription() != null ? book.getDescription() : "Trama non disponibile.";
            stmt.setString(6, desc);

            // LA MAGIA DEL TEMPO: Se lo stato è "IN LETTURA" (READING), salviamo la data di oggi!
            java.sql.Date sqlDate = null;
            if ("READING".equalsIgnoreCase(readingStatus) || "IN LETTURA".equalsIgnoreCase(readingStatus)) {
                sqlDate = java.sql.Date.valueOf(LocalDate.now());
            }
            stmt.setDate(7, sqlDate); // Per l'INSERT

            // Aggiornamenti in caso di duplicato
            stmt.setString(8, readingStatus);
            stmt.setString(9, desc);
            stmt.setDate(10, sqlDate); // Per l'UPDATE

            stmt.executeUpdate();

            // 3. Sincronizzazione automatica delle vendite se il libro viene segnato come LETTO
            if (ReadingStatus.READ.name().equalsIgnoreCase(readingStatus) || "LETTO".equalsIgnoreCase(readingStatus)) {
                String updateSales = "UPDATE published_books SET copie_vendute = copie_vendute + 1 WHERE title = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSales)) {
                    updateStmt.setString(1, book.getTitle());
                    updateStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            throw new DAOException("Errore nell'aggiunta del libro ai preferiti: " + e.getMessage());
        }
    }

    @Override
    public void removeFavoriteBook(String username, String bookTitle) throws DAOException {
        String query = "DELETE FROM preferiti WHERE username = ? AND titolo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, bookTitle);
            stmt.executeUpdate();
        } catch (SQLException e) { throw new DAOException(e.getMessage()); }
    }

    @Override
    public void updateRating(String username, String bookTitle, int rating) throws DAOException {
        String query = "UPDATE preferiti SET valutazione = ? WHERE username = ? AND titolo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, rating);
            stmt.setString(2, username);
            stmt.setString(3, bookTitle);
            stmt.executeUpdate();
        } catch (SQLException e) { throw new DAOException(e.getMessage()); }
    }

    @Override
    public List<Book> getBooksByStatus(String username, String readingStatus) throws DAOException {
        List<Book> lista = new ArrayList<>();

        String query = "SELECT titolo, autore, immagine_url, descrizione, valutazione, stato_lettura, data_inizio_lettura " +
                "FROM preferiti WHERE username = ? AND stato_lettura = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, readingStatus);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    // Assicurati che i nomi delle colonne coincidano con quelli del tuo DB
                    book.setTitle(rs.getString("titolo"));
                    book.setAuthor(rs.getString("autore"));
                    book.setImageUrl(rs.getString("immagine_url"));
                    book.setDescription(rs.getString("descrizione"));
                    book.setRating(rs.getInt("valutazione"));

                    if (rs.getDate("data_inizio_lettura") != null) {
                        book.setReadingStartDate(rs.getDate("data_inizio_lettura").toLocalDate());
                    }

                    ReadingStatus statusEnum = readingStatus != null ? ReadingStatus.valueOf(readingStatus) : null;
                    book.setStatus(statusEnum);

                    lista.add(book);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Errore durante il recupero dei libri: " + e.getMessage());
        }

        return lista;
    }
}