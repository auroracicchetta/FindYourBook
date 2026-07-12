
package it.ispwproject.findyourbook.dao;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.model.Publisher;
import it.ispwproject.findyourbook.model.PublisherStats;
import java.util.List;

public interface PublisherDAO {

    Publisher findById(int id) throws DAOException;
    PublisherStats getPublisherStatistics(String publisherUsername) throws DAOException;


    void publishBook(BookBean book, String publisherUsername) throws DAOException;
    List<Book> getCatalogByPublisher(String username) throws DAOException;


    void updateBook(BookBean book, String publisherUsername) throws DAOException;
    void deleteBook(String bookTitle, String publisherUsername) throws DAOException;

}