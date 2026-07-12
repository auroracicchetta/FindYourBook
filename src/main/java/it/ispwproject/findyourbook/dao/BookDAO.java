package it.ispwproject.findyourbook.dao;

import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Book;
import java.util.List;

public interface BookDAO {
    List<Book> findByGenre(String genre) throws DAOException;
    List<Book> searchByQuery(String query) throws DAOException;
    void save(Book book) throws DAOException; // Aggiunto per il tuo metodo
}