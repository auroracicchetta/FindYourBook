package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.dao.DAOFactory;
import it.ispwproject.findyourbook.demo.DemoDataStore;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import it.ispwproject.findyourbook.model.Reader;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * ------------------------------------------------------------
 * Test Class : UserLibraryControllerTest
 * Description: Verifica il corretto salvataggio e aggiornamento
 * degli stati di lettura dei libri nella libreria del lettore
 * in modalità MEMORY.
 * ------------------------------------------------------------
 */
class UserLibraryControllerTest {

    private UserLibraryController userLibraryController;

    @BeforeEach
    void setup() {
        DemoDataStore.reset();
        DAOFactory.setPersistence(DAOFactory.MEMORY);

        Reader reader = new Reader(1, "Mario", "Rossi", "mario", "mario@test.it", "hash", LocalDate.now(), LocalDate.of(1995, Month.MAY, 20));
        SessionManager.getInstance().setLoggedUser(reader);

        userLibraryController = new UserLibraryController();
    }

    @Test
    void testSalvataggioLibroInLibreria() {
        BookBean bookBean = new BookBean("Il Signore degli Anelli", "J.R.R. Tolkien", "fantasy", "url", "trama");

        assertDoesNotThrow(() ->
                userLibraryController.saveBookToLibrary(bookBean, ReadingStatus.TO_READ)
        );
    }
}