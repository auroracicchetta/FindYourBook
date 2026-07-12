package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.bean.RegistrationBean;
import it.ispwproject.findyourbook.dao.DAOFactory;
import it.ispwproject.findyourbook.demo.DemoDataStore;
import it.ispwproject.findyourbook.enumerator.Role;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.exception.RegistrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ------------------------------------------------------------
 * Test Class : RegistrationControllerTest
 * Description: Verifica che il sistema impedisca la registrazione
 * di due account con lo stesso username/email.
 * Dopo una prima registrazione avvenuta con successo,
 * un secondo tentativo con lo stesso username deve
 * lanciare una RegistrationException.
 * ------------------------------------------------------------
 */
class RegistrationControllerTest {

    private RegistrationController registrationController;

    @BeforeEach
    void setup() {
        DemoDataStore.reset();
        DAOFactory.setPersistence(DAOFactory.MEMORY);
        registrationController = new RegistrationController();
    }

    @Test
    void testRegistrazioneConUsernameDuplicato() throws DAOException, RegistrationException {
        // Prima registrazione — deve andare a buon fine
        RegistrationBean bean = new RegistrationBean();
        bean.setName("Mario");
        bean.setSurname("Rossi");
        bean.setUsername("mariorossi");
        bean.setEmail("mario@test.com");
        bean.setPassword("Password123!");
        bean.setConfirmPassword("Password123!");
        bean.setRole(Role.READER);
        bean.setBirthDate(LocalDate.now().minusYears(20));

        registrationController.register(bean);

        // Seconda registrazione con lo stesso username — deve lanciare RegistrationException
        RegistrationBean duplicato = new RegistrationBean();
        duplicato.setName("Luigi");
        duplicato.setSurname("Verdi");
        duplicato.setUsername("mariorossi"); // Username già esistente
        duplicato.setEmail("luigi@test.com");
        duplicato.setPassword("Password123!");
        duplicato.setConfirmPassword("Password123!");
        duplicato.setRole(Role.READER);
        duplicato.setBirthDate(LocalDate.now().minusYears(22));

        assertThrows(RegistrationException.class, () ->
                registrationController.register(duplicato)
        );
    }
}