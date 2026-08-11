package it.ispwproject.findyourbook.pattern.observer;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.bean.PublisherBean;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.model.Publisher;
import it.ispwproject.findyourbook.service.NotificationService;
import it.ispwproject.findyourbook.exception.NotificationException;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public class BookPublishedObserver implements Observer {
    private final Publisher publisher;
    private final Book book;

    public BookPublishedObserver(Publisher publisher, Book book) {
        this.publisher = publisher;
        this.book = book;
    }

    @Override
    public void update() {
        try {
            NotificationService.sendBookPublishedNotification(buildPublisherBean(), buildBookBean());
        } catch (NotificationException e) {
            AppLogger.logWarning("Notifica pubblicazione non inviata: " + e.getMessage());
        }
    }

    private PublisherBean buildPublisherBean() {
        return new PublisherBean(
                publisher.getUsername(), publisher.getName(), publisher.getSurname(),
                publisher.getEmail(), publisher.getRegistrationDate(),
                publisher.getDescription());
    }

    private BookBean buildBookBean() {
        return new BookBean(book.getTitle(), book.getAuthor(), book.getGenre(),
                book.getImageUrl(), book.getDescription());
    }
}