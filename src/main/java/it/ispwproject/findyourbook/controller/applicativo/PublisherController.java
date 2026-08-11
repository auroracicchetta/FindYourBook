package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.dao.DAOFactory;
import it.ispwproject.findyourbook.dao.PublisherDAO;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.model.Publisher;
import it.ispwproject.findyourbook.pattern.observer.BookPublishedObserver;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class PublisherController {

    private final PublisherDAO publisherDAO;

    public PublisherController() {
        this.publisherDAO = DAOFactory.getPublisherDAO();
    }

    public void publishNewBook(BookBean bookBean) throws DAOException {

        Publisher loggedUser = (Publisher) SessionManager.getInstance().getLoggedUser();
        String publisherUsername = loggedUser.getUsername();

        publisherDAO.publishBook(bookBean, publisherUsername);

        Book book = new Book();
        book.setTitle(bookBean.getTitle());
        book.setAuthor(bookBean.getAuthor());
        book.setGenre(bookBean.getGenre());
        book.setImageUrl(bookBean.getImageUrl());
        book.setDescription(bookBean.getDescription());

        BookPublishedObserver observer = new BookPublishedObserver(loggedUser, book);

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
            bean.setCopieLette(b.getCopieLette());
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