package it.ispwproject.findyourbook.dao.db;

import it.ispwproject.findyourbook.dao.ConnectionFactory;
import it.ispwproject.findyourbook.dao.UserDAO;
import it.ispwproject.findyourbook.enumerator.Role;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Publisher;
import it.ispwproject.findyourbook.model.Reader;
import it.ispwproject.findyourbook.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDAODB implements UserDAO {

    private static final String FIND_BY_USERNAME =
            "SELECT id, nome, cognome, username, password, email, ruolo, data_registrazione, data_nascita, descrizione " +
                    "FROM utenti WHERE username = ?";

    private static final String GET_ALL =
            "SELECT id, nome, cognome, username, password, email, ruolo, data_registrazione, data_nascita, descrizione FROM utenti";

    private static final String UPDATE_EMAIL = "UPDATE utenti SET email = ? WHERE username = ?";

    @Override
    public User findByUsername(String username) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_USERNAME)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new DAOException("Utente non trovato: " + username);

                int id = rs.getInt("id");
                String name = rs.getString("nome");
                String surname = rs.getString("cognome");
                String user = rs.getString("username");
                String pass = rs.getString("password");
                String email = rs.getString("email");
                Role role = Role.fromString(rs.getString("ruolo")); // Usa il fromString allineato

                java.sql.Date sqlRegDate = rs.getDate("data_registrazione");
                LocalDate regDate = (sqlRegDate != null) ? sqlRegDate.toLocalDate() : LocalDate.now();

                java.sql.Date sqlBirthDate = rs.getDate("data_nascita");
                LocalDate birthDate = (sqlBirthDate != null) ? sqlBirthDate.toLocalDate() : null;

                String description = rs.getString("descrizione");

                return buildUser(id, name, surname, user, email, pass, regDate, birthDate, role, description);
            }
        } catch (SQLException e) {
            throw new DAOException("Errore caricamento utente: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getAll() throws DAOException {
        List<User> result = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("nome");
                String surname = rs.getString("cognome");
                String user = rs.getString("username");
                String pass = rs.getString("password");
                String email = rs.getString("email");
                Role role = Role.fromString(rs.getString("ruolo"));

                java.sql.Date sqlRegDate = rs.getDate("data_registrazione");
                LocalDate regDate = (sqlRegDate != null) ? sqlRegDate.toLocalDate() : LocalDate.now();

                java.sql.Date sqlBirthDate = rs.getDate("data_nascita");
                LocalDate birthDate = (sqlBirthDate != null) ? sqlBirthDate.toLocalDate() : null;

                String description = rs.getString("descrizione");

                result.add(buildUser(id, name, surname, user, email, pass, regDate, birthDate, role, description));
            }
        } catch (SQLException e) {
            throw new DAOException("Errore caricamento utenti: " + e.getMessage(), e);
        }
        return result;
    }

    private User buildUser(int id, String name, String surname, String username, String email,
                           String password, LocalDate regDate, LocalDate birthDate, Role role, String description) {
        return switch (role) {
            case READER -> new Reader(id, name, surname, username, email, password, regDate, birthDate);
            case PUBLISHER -> new Publisher(id, name, surname, username, email, password, regDate, description);
            default -> throw new IllegalStateException("Ruolo non riconosciuto: " + role);
        };
    }

    @Override
    public void updateEmail(String username, String newEmail) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_EMAIL)) {

            ps.setString(1, newEmail);
            ps.setString(2, username);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Utente non trovato per l'aggiornamento email: " + username);
            }
        } catch (SQLException e) {
            throw new DAOException("Errore aggiornamento email: " + e.getMessage(), e);
        }
    }
}