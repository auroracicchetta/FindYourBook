package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.dao.DAOFactory;
import it.ispwproject.findyourbook.demo.DemoDataStore;
import it.ispwproject.findyourbook.exception.LoginException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ------------------------------------------------------------
 * Test Class : LoginControllerTest
 * Description: Verifica la corretta gestione del login con
 * credenziali non valide. Il sistema deve rifiutare
 * l'accesso e lanciare una LoginException quando
 * l'utente non è registrato nella piattaforma.
 * ------------------------------------------------------------
 */
class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    void setup() {
        DemoDataStore.reset();
        DAOFactory.setPersistence(DAOFactory.MEMORY);
        loginController = new LoginController();
    }

    @Test
    void testLoginConCredenzialiErrate() {
        // Tenta il login con un utente/email non registrata
        // Deve lanciare LoginException
        assertThrows(LoginException.class, () ->
                loginController.login("nonregistrato@demo", "password123")
        );
    }
}