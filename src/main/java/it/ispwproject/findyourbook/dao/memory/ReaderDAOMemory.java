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
        List<Book> userFavorites = favorites.get(username);

        // Stato precedente PRIMA di rimuovere la vecchia voce: serve per capire se
        // il contatore copie_lette va incrementato, decrementato o lasciato invariato.
        ReadingStatus previousStatus = userFavorites.stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(book.getTitle()))
                .map(Book::getStatus)
                .findFirst()
                .orElse(null);

        userFavorites.removeIf(b -> b.getTitle().equalsIgnoreCase(book.getTitle()));

        ReadingStatus statusEnum = status != null ? ReadingStatus.valueOf(status) : null;
        book.setStatus(statusEnum);

        if (ReadingStatus.READING.equals(statusEnum)) {
            book.setReadingStartDate(LocalDate.now(java.time.ZoneId.systemDefault()));
        }


        userFavorites.add(book);

        store.getBooks().stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(book.getTitle()))
                .findFirst()
                .ifPresent(b -> b.setStatus(statusEnum));

        boolean wasRead = ReadingStatus.READ.equals(previousStatus);
        boolean isRead = ReadingStatus.READ.equals(statusEnum) || "LETTO".equalsIgnoreCase(status);

        // copie_lette rappresenta quanti lettori hanno ORA il libro segnato come
        // Letto: si tocca solo quando lo stato READ cambia davvero (entra o esce),
        // mai su un semplice "riconferma" dello stesso stato.
        if (isRead && !wasRead) {
            store.getBooks().stream()
                    .filter(b -> b.getTitle().equalsIgnoreCase(book.getTitle()))
                    .findFirst()
                    .ifPresent(b -> b.setCopieLette(b.getCopieLette() + 1));
        } else if (!isRead && wasRead) {
            store.getBooks().stream()
                    .filter(b -> b.getTitle().equalsIgnoreCase(book.getTitle()))
                    .findFirst()
                    .ifPresent(b -> b.setCopieLette(Math.max(0, b.getCopieLette() - 1)));
        }
    }

    @Override
    public void removeFavoriteBook(String username, String title) throws DAOException {
        if (store.getFavorites().containsKey(username)) {
            List<Book> userFavorites = store.getFavorites().get(username);

            // Se il libro rimosso era segnato come Letto, il contatore va
            // decrementato: altrimenti resterebbe gonfiato anche se il lettore
            // toglie del tutto il libro dalla libreria.
            ReadingStatus previousStatus = userFavorites.stream()
                    .filter(b -> b.getTitle().equalsIgnoreCase(title))
                    .map(Book::getStatus)
                    .findFirst()
                    .orElse(null);

            userFavorites.removeIf(b -> b.getTitle().equalsIgnoreCase(title));

            if (ReadingStatus.READ.equals(previousStatus)) {
                store.getBooks().stream()
                        .filter(b -> b.getTitle().equalsIgnoreCase(title))
                        .findFirst()
                        .ifPresent(b -> b.setCopieLette(Math.max(0, b.getCopieLette() - 1)));
            }
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