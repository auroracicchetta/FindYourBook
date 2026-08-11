package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.dao.DAOFactory;
import it.ispwproject.findyourbook.dao.ReaderDAO;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.model.Reader;
import it.ispwproject.findyourbook.pattern.observer.BookCompletedObserver;
import it.ispwproject.findyourbook.pattern.observer.ReadingReminderObserver;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.util.logger.AppLogger;

import java.util.concurrent.*;

import java.util.ArrayList;
import java.util.List;

public class UserLibraryController {

    private final ReaderDAO readerDAO;
    private static final ExecutorService NOTIFICATION_EXECUTOR = Executors.newCachedThreadPool();

    public UserLibraryController() {
        this.readerDAO = DAOFactory.getReaderDAO();
    }

    public void saveBookToLibrary(BookBean bookBean, ReadingStatus status) throws DAOException {
        Reader reader = (Reader) SessionManager.getInstance().getLoggedUser();
        if (reader == null) throw new DAOException("Utente non loggato");

        Book book = new Book();
        book.setTitle(bookBean.getTitle());
        book.setAuthor(bookBean.getAuthor());
        book.setGenre(bookBean.getGenre());
        book.setImageUrl(bookBean.getImageUrl());
        book.setDescription(bookBean.getDescription());
        book.setRating(bookBean.getRating());
        book.setStatus(status);

        readerDAO.addFavoriteBook(reader.getUsername(), book, status.name());

        if (status == ReadingStatus.READ) {

            // Invio asincrono
            BookCompletedObserver observer = new BookCompletedObserver(reader, book);
            NOTIFICATION_EXECUTOR.submit(() -> {
                book.attach(observer);
                book.markAsRead();
                book.detach(observer);
            });
        }
    }

    public void removeBookFromLibrary(BookBean bookBean) throws DAOException {
        Reader reader = (Reader) SessionManager.getInstance().getLoggedUser();
        readerDAO.removeFavoriteBook(reader.getUsername(), bookBean.getTitle());
    }

    public void rateBook(BookBean bookBean, int rating) throws DAOException {
        Reader reader = (Reader) SessionManager.getInstance().getLoggedUser();

        readerDAO.updateRating(reader.getUsername(), bookBean.getTitle(), rating);
        bookBean.setRating(rating);
    }

    public void syncBooksWithDatabase(List<BookBean> searchResults) throws DAOException {
        Reader reader = (Reader) SessionManager.getInstance().getLoggedUser();
        if (reader == null) return;

        List<Book> savedBooks = new ArrayList<>(readerDAO.getBooksByStatus(reader.getUsername(), ReadingStatus.READ.name()));
        savedBooks.addAll(readerDAO.getBooksByStatus(reader.getUsername(), ReadingStatus.TO_READ.name()));
        savedBooks.addAll(readerDAO.getBooksByStatus(reader.getUsername(), ReadingStatus.READING.name()));

        for (BookBean bean : searchResults) {
            checkAndSync(bean, savedBooks);
        }
    }

    private void checkAndSync(BookBean bean, List<Book> savedBooks) {

        Book match = savedBooks.stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(bean.getTitle()))
                .findFirst()
                .orElse(null);

        if (match != null) {
            bean.setRating(match.getRating());
            bean.setStatus(match.getStatus());
            bean.setDescription(match.getDescription());
        } else {
            bean.setRating(0);
            bean.setStatus(null);
        }
    }

    public void checkInactiveReading() {

        if (SessionManager.getInstance().isInactivityReminderSent()) return;

        Reader reader = (Reader) SessionManager.getInstance().getLoggedUser();
        if (reader == null) return;

        try {
            List<Book> readingBooks = readerDAO.getBooksByStatus(reader.getUsername(), ReadingStatus.READING.name());
            java.time.LocalDate thirtyDaysAgo = java.time.LocalDate.now(java.time.ZoneId.systemDefault()).minusDays(30);

            for (Book book : readingBooks) {
                if (book.getReadingStartDate() != null && book.getReadingStartDate().isBefore(thirtyDaysAgo)) {

                    ReadingReminderObserver observer = new ReadingReminderObserver(reader, book);

                    NOTIFICATION_EXECUTOR.submit(() -> {
                        book.attach(observer);
                        book.triggerReminder();
                        book.detach(observer);
                    });

                    SessionManager.getInstance().setInactivityReminderSent(true);
                    break;
                }
            }
        } catch (Exception e) {
            AppLogger.logError("Errore durante il controllo delle letture inattive: " + e.getMessage());
        }
    }
}