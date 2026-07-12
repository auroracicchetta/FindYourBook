package it.ispwproject.findyourbook.dao;

import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.model.Reader;
import java.util.List;

public interface ReaderDAO {
    Reader findById(int id) throws DAOException;

    void addFavoriteBook(String username, Book book, String readingStatus) throws DAOException;

    void removeFavoriteBook(String username, String bookTitle) throws DAOException;

    List<Book> getBooksByStatus(String username, String readingStatus) throws DAOException;

    void updateRating(String username, String bookTitle, int rating) throws DAOException;
}