package it.ispwproject.findyourbook.dao.memory;

import it.ispwproject.findyourbook.dao.BookDAO;
import it.ispwproject.findyourbook.demo.DemoDataStore;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Book;
import java.util.List;

public class BookDAOMemory implements BookDAO {

    @Override
    public List<Book> findByGenre(String genre) throws DAOException {
        return DemoDataStore.getInstance().getBooks().stream()
                .filter(b -> b.getGenre().equalsIgnoreCase(genre))
                .toList();
    }

    @Override
    public void save(Book book) throws DAOException {
        // Generiamo un ID fittizio e aggiungiamo il libro alla lista in memoria
        book.setId(DemoDataStore.getInstance().getBooks().size() + 1);
        DemoDataStore.getInstance().getBooks().add(book);
    }

    @Override
    public List<Book> searchByQuery(String query) throws DAOException {
        String lower = query.toLowerCase();
        return DemoDataStore.getInstance().getBooks().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lower) || b.getAuthor().toLowerCase().contains(lower))
                .toList();
    }
}