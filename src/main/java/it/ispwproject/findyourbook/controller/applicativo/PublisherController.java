package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.dao.DAOFactory;
import it.ispwproject.findyourbook.dao.PublisherDAO;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.model.User;
import it.ispwproject.findyourbook.pattern.observer.BookPublishedObserver;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.service.NotificationService;
import it.ispwproject.findyourbook.util.logger.AppLogger;

import java.util.ArrayList;
import java.util.List;

public class PublisherController {

    private final PublisherDAO publisherDAO;

    public PublisherController() {
        this.publisherDAO = DAOFactory.getPublisherDAO();
    }

    public void publishNewBook(BookBean bookBean) throws DAOException {

        User loggedUser = SessionManager.getInstance().getLoggedUser();
        String publisherUsername = loggedUser.getUsername();
        String publisherEmail = loggedUser.getEmail();
        String publisherName = loggedUser.getName();

        publisherDAO.publishBook(bookBean, publisherUsername); // Riga già presente

        BookPublishedObserver observer = new BookPublishedObserver(
                publisherEmail,
                publisherName,
                bookBean.getTitle()
        );

        Book book = new Book();
        book.setTitle(bookBean.getTitle());

        book.attach(observer);
        book.markAsPublished();
        book.detach(observer);

    }

    public List<BookBean> getMyCatalog() throws DAOException {
        String username = SessionManager.getInstance().getLoggedUser().getUsername();

        List<Book> bookModels = publisherDAO.getCatalogByPublisher(username);
        List<BookBean> beans = new ArrayList<>();

        for (Book b : bookModels) {
            BookBean bean = new BookBean();
            bean.setTitle(b.getTitle());
            bean.setAuthor(b.getAuthor());
            bean.setGenre(b.getGenre());
            bean.setDescription(b.getDescription());
            bean.setImageUrl(b.getImageUrl());
            bean.setCopieVendute(b.getCopieVendute());
            beans.add(bean);
        }

        return beans;
    }

    public void updateExistingBook(BookBean updatedBook) throws DAOException {
        String publisherUsername = SessionManager.getInstance().getLoggedUser().getUsername();
        publisherDAO.updateBook(updatedBook, publisherUsername);
    }

    public void removeBookFromCatalog(String bookTitle) throws DAOException {
        String publisherUsername = SessionManager.getInstance().getLoggedUser().getUsername();
        publisherDAO.deleteBook(bookTitle, publisherUsername);
    }
}