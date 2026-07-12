package it.ispwproject.findyourbook.pattern.observer;

import it.ispwproject.findyourbook.service.NotificationService;
import it.ispwproject.findyourbook.exception.NotificationException;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public class BookCompletedObserver implements Observer {
    private final String email;
    private final String readerName;
    private final String bookTitle;

    public BookCompletedObserver(String email, String readerName, String bookTitle) {
        this.email = email;
        this.readerName = readerName;
        this.bookTitle = bookTitle;
    }

    @Override
    public void update() {
        try {
            NotificationService.sendReadingGoalReachedNotification(email, readerName, bookTitle);
        } catch (NotificationException e) {
            AppLogger.logWarning("Notifica completamento libro non inviata: " + e.getMessage());
        }
    }
}