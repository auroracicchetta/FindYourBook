package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.dao.DAOFactory;
import it.ispwproject.findyourbook.dao.ReaderDAO;
import it.ispwproject.findyourbook.demo.DemoDataStore;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.model.Reader;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ------------------------------------------------------------
 * Test Class : UserLibraryControllerTest
 * Description: Verifica il corretto salvataggio e aggiornamento
 * degli stati di lettura dei libri nella libreria del lettore
 * in modalità MEMORY. A differenza di un semplice controllo di
 * "nessuna eccezione lanciata", ogni test recupera lo stato reale
 * salvato tramite il ReaderDAO e verifica che corrisponda a quanto
 * atteso.
 * ------------------------------------------------------------
 */
class UserLibraryControllerTest {

    private UserLibraryController userLibraryController;
    private ReaderDAO readerDAO;
    private Reader reader;

    @BeforeEach
    void setup() {
        DemoDataStore.reset();
        DAOFactory.setPersistence(DAOFactory.MEMORY);

        reader = new Reader(1, "Mario", "Rossi", "mario", "mario@test.it", "hash", LocalDate.now(), LocalDate.of(1995, Month.MAY, 20));
        SessionManager.getInstance().setLoggedUser(reader);

        userLibraryController = new UserLibraryController();
        readerDAO = DAOFactory.getReaderDAO();
    }

    @Test
    void testSalvataggioLibroInLibreria() throws DAOException {
        BookBean bookBean = new BookBean("Il Signore degli Anelli", "J.R.R. Tolkien", "fantasy", "url", "trama");

        assertDoesNotThrow(() ->
                userLibraryController.saveBookToLibrary(bookBean, ReadingStatus.TO_READ)
        );

        List<Book> daLeggere = readerDAO.getBooksByStatus(reader.getUsername(), ReadingStatus.TO_READ.name());
        assertTrue(daLeggere.stream().anyMatch(b -> b.getTitle().equalsIgnoreCase(bookBean.getTitle())),
                "Il libro salvato deve comparire nella sezione 'Da leggere'.");
    }

    @Test
    void testAggiornamentoStatoLibroInLibreria() throws DAOException {
        BookBean bookBean = new BookBean("Il Signore degli Anelli", "J.R.R. Tolkien", "fantasy", "url", "trama");

        // Prima aggiunta: il libro entra in libreria come "Da leggere"
        userLibraryController.saveBookToLibrary(bookBean, ReadingStatus.TO_READ);

        // Aggiornamento dello stato: il lettore inizia la lettura
        userLibraryController.saveBookToLibrary(bookBean, ReadingStatus.READING);

        List<Book> daLeggere = readerDAO.getBooksByStatus(reader.getUsername(), ReadingStatus.TO_READ.name());
        List<Book> inLettura = readerDAO.getBooksByStatus(reader.getUsername(), ReadingStatus.READING.name());

        assertTrue(daLeggere.stream().noneMatch(b -> b.getTitle().equalsIgnoreCase(bookBean.getTitle())),
                "Dopo l'aggiornamento il libro non deve più comparire nella vecchia sezione 'Da leggere'.");
        assertTrue(inLettura.stream().anyMatch(b -> b.getTitle().equalsIgnoreCase(bookBean.getTitle())),
                "Dopo l'aggiornamento il libro deve comparire nella nuova sezione 'In lettura'.");
    }
}