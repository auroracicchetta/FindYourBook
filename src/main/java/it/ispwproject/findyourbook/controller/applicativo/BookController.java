package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.dao.BookDAO;
import it.ispwproject.findyourbook.dao.DAOFactory;
import it.ispwproject.findyourbook.dao.ReaderDAO;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.util.logger.AppLogger;
import it.ispwproject.findyourbook.exception.DAOException;

import java.util.ArrayList;
import java.util.List;

public class BookController {

    private final BookDAO bookDAO;
    private final ReaderDAO readerDAO;

    public BookController() {
        this.bookDAO = DAOFactory.getBookDAO();
        this.readerDAO = DAOFactory.getReaderDAO();
    }

    public List<BookBean> getBooksByGenre(String genre) {
        List<BookBean> results = new ArrayList<>();
        try {

            List<Book> libriDb = bookDAO.findByGenre(genre.toLowerCase());

            for (Book b : libriDb) {
                BookBean bean = new BookBean(b.getTitle(), b.getAuthor(), b.getGenre(), b.getImageUrl(), b.getDescription());
                results.add(bean);
            }

        } catch (Exception e) {
            AppLogger.logError("Errore nel recupero dei libri per genere: " + e.getMessage());
        }
        return results;
    }

    public List<BookBean> getFavoriteBooks(String username, ReadingStatus status) {
        List<BookBean> results = new ArrayList<>();
        try {
            List<Book> savedBooks = readerDAO.getBooksByStatus(username, status.name());

            for (Book b : savedBooks) {
                BookBean bean = new BookBean(b.getTitle(), b.getAuthor(), b.getGenre(), b.getImageUrl(), b.getDescription());
                bean.setRating(b.getRating());
                bean.setStatus(b.getStatus());
                results.add(bean);
            }

        } catch (DAOException e) {
            AppLogger.logError("Errore nel recupero dei libri preferiti: " + e.getMessage());
        }
        return results;
    }

    public List<BookBean> searchBooks(String query) {
        List<BookBean> results = new ArrayList<>();
        try {
            List<Book> searchDb = bookDAO.searchByQuery(query);
            for (Book b : searchDb) {
                results.add(new BookBean(b.getTitle(), b.getAuthor(), b.getGenre(), b.getImageUrl(), b.getDescription()));
            }
            AppLogger.logInfo("Ricerca locale completata per: " + query);
        } catch (Exception e) {
            AppLogger.logError("Errore durante la ricerca locale: " + e.getMessage());
        }
        return results;
    }

}