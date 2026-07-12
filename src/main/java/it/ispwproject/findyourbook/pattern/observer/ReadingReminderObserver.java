package it.ispwproject.findyourbook.pattern.observer;

import it.ispwproject.findyourbook.service.NotificationService;
import it.ispwproject.findyourbook.exception.NotificationException;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public class ReadingReminderObserver implements Observer {
    private final String email;
    private final String readerName;
    private final String bookTitle;

    public ReadingReminderObserver(String email, String readerName, String bookTitle) {
        this.email = email;
        this.readerName = readerName;
        this.bookTitle = bookTitle;
    }

    @Override
    public void update() {
        try {
            NotificationService.sendReadingReminder(email, readerName, bookTitle);
        } catch (NotificationException e) {
            AppLogger.logWarning("Notifica promemoria inattività non inviata: " + e.getMessage());
        }
    }
}