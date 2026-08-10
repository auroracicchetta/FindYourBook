package it.ispwproject.findyourbook.pattern.observer;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.bean.ReaderBean;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.model.Reader;
import it.ispwproject.findyourbook.service.NotificationService;
import it.ispwproject.findyourbook.exception.NotificationException;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public class ReadingReminderObserver implements Observer {
    private final Reader reader;
    private final Book book;

    public ReadingReminderObserver(Reader reader, Book book) {
        this.reader = reader;
        this.book = book;
    }

    @Override
    public void update() {
        try {
            NotificationService.sendReadingReminder(buildReaderBean(), buildBookBean());
        } catch (NotificationException e) {
            AppLogger.logWarning("Notifica promemoria inattività non inviata: " + e.getMessage());
        }
    }

    private ReaderBean buildReaderBean() {
        return new ReaderBean(reader.getUsername(), reader.getName(), reader.getSurname(),
                reader.getEmail(), reader.getBirthDate(), reader.getRegistrationDate());
    }

    private BookBean buildBookBean() {
        return new BookBean(book.getTitle(), book.getAuthor(), book.getGenre(),
                book.getImageUrl(), book.getDescription());
    }
}