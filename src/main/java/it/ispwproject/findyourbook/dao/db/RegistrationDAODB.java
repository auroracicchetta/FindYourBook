package it.ispwproject.findyourbook.dao.db;

import it.ispwproject.findyourbook.dao.ConnectionFactory;
import it.ispwproject.findyourbook.dao.RegistrationDAO;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Publisher;
import it.ispwproject.findyourbook.model.Reader;
import it.ispwproject.findyourbook.model.User;

import java.sql.*;

public class RegistrationDAODB implements RegistrationDAO {

    private static final String CHECK_USERNAME =
            "SELECT COUNT(*) FROM utenti WHERE username = ?";

    private static final String CHECK_EMAIL =
            "SELECT COUNT(*) FROM utenti WHERE email = ?";

    private static final String INSERT_USER =
            "INSERT INTO utenti (nome, cognome, username, password, email, ruolo, data_nascita, descrizione, data_registrazione) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public boolean usernameExists(String username) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(CHECK_USERNAME)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            throw new DAOException("Errore verifica username: " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean emailExists(String email) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(CHECK_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            throw new DAOException("Errore verifica email: " + e.getMessage());
        }

        return false;
    }

    @Override
    public void save(User user) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_USER)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getPassword());

            ps.setString(5, user.getEmail());

            if (user instanceof Publisher pub) {
                ps.setString(6, "PUBLISHER");
                ps.setNull(7, Types.DATE);
                ps.setString(8, pub.getDescription());
            } else if (user instanceof Reader reader) {
                ps.setString(6, "READER");
                ps.setDate(7, java.sql.Date.valueOf(reader.getBirthDate()));
                ps.setNull(8, Types.VARCHAR);
            }

            ps.setDate(9, java.sql.Date.valueOf(user.getRegistrationDate()));

            ps.executeUpdate();

        } catch (Exception e) {
            throw new DAOException("Errore durante la registrazione nel DB: " + e.getMessage());
        }
    }
}