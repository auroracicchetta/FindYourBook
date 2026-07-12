package it.ispwproject.findyourbook.dao.db;

import it.ispwproject.findyourbook.dao.ConnectionFactory;
import it.ispwproject.findyourbook.dao.LoginDAO;
import it.ispwproject.findyourbook.enumerator.Role;
import it.ispwproject.findyourbook.exception.LoginException;
import it.ispwproject.findyourbook.model.Credentials;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class LoginDAODB implements LoginDAO {

    @Override
    public Credentials execute(String username, String plainPassword) throws LoginException {
        String hashedPassword = plainPassword;


        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs = conn.prepareCall("{call login(?, ?, ?, ?, ?, ?)}")) {

            cs.setString(1, username);
            cs.setString(2, hashedPassword);

            cs.registerOutParameter(3, Types.INTEGER);
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.registerOutParameter(5, Types.VARCHAR);
            cs.registerOutParameter(6, Types.VARCHAR);


            cs.execute();

            String roleStr = cs.getString(6);

            if (roleStr == null || roleStr.equals("NOT_FOUND")) {
                throw new LoginException("Credenziali non valide. Riprova.");
            }

            return new Credentials(username, hashedPassword, Role.fromString(roleStr));

        } catch (SQLException e) {
            throw new LoginException("Errore DB: " + e.getMessage(), e);
        }
    }
}