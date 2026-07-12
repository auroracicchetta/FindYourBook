package it.ispwproject.findyourbook.pattern.observer;

import it.ispwproject.findyourbook.service.NotificationService;
import it.ispwproject.findyourbook.exception.NotificationException;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public class BookPublishedObserver implements Observer {
    private final String email;
    private final String publisherName;
    private final String bookTitle;

    public BookPublishedObserver(String email, String publisherName, String bookTitle) {
        this.email = email;
        this.publisherName = publisherName;
        this.bookTitle = bookTitle;
    }

    @Override
    public void update() {
        try {
            NotificationService.sendBookPublishedNotification(email, publisherName, bookTitle);
        } catch (NotificationException e) {
            AppLogger.logWarning("Notifica pubblicazione non inviata: " + e.getMessage());
        }
    }
}