package it.ispwproject.findyourbook.dao.memory;

import it.ispwproject.findyourbook.dao.ReaderDAO;
import it.ispwproject.findyourbook.demo.DemoDataStore;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.model.Reader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReaderDAOMemory implements ReaderDAO {
    private final DemoDataStore store = DemoDataStore.getInstance();

    @Override
    public Reader findById(int id) throws DAOException {
        return store.getUsers().stream()
                .filter(u -> u instanceof Reader && u.getId() == id)
                .map(Reader.class::cast)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addFavoriteBook(String username, Book book, String status) throws DAOException {
        var favorites = store.getFavorites();
        favorites.computeIfAbsent(username, k -> new ArrayList<>());

        favorites.get(username).removeIf(b -> b.getTitle().equalsIgnoreCase(book.getTitle()));

        ReadingStatus statusEnum = status != null ? ReadingStatus.valueOf(status) : null;
        book.setStatus(statusEnum);

        if (ReadingStatus.READING.equals(statusEnum)) {
            book.setReadingStartDate(LocalDate.now(java.time.ZoneId.systemDefault()));
        }


        favorites.get(username).add(book);

        store.getBooks().stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(book.getTitle()))
                .findFirst()
                .ifPresent(b -> b.setStatus(statusEnum));

        if (ReadingStatus.READ.name().equalsIgnoreCase(status) || "LETTO".equalsIgnoreCase(status)) {
            store.getBooks().stream()
                    .filter(b -> b.getTitle().equalsIgnoreCase(book.getTitle()))
                    .findFirst()
                    .ifPresent(b -> b.setCopieVendute(b.getCopieVendute() + 1));
        }
    }

    @Override
    public void removeFavoriteBook(String username, String title) throws DAOException {
        if (store.getFavorites().containsKey(username)) {
            store.getFavorites().get(username).removeIf(b -> b.getTitle().equalsIgnoreCase(title));
        }
        store.getBooks().stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .ifPresent(b -> b.setStatus(null));
    }

    @Override
    public void updateRating(String username, String title, int rating) throws DAOException {
        List<Book> userBooks = store.getFavorites().get(username);
        if (userBooks != null) {
            userBooks.stream()
                    .filter(b -> b.getTitle().equalsIgnoreCase(title))
                    .findFirst()
                    .ifPresent(b -> b.setRating(rating));
        }
        store.getBooks().stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .ifPresent(b -> b.setRating(rating));
    }

    @Override
    public List<Book> getBooksByStatus(String username, String status) throws DAOException {
        return store.getFavorites().getOrDefault(username, new ArrayList<>()).stream()
                .filter(b -> status != null && b.getStatus() != null && status.equalsIgnoreCase(b.getStatus().name()))
                .toList();
    }
}