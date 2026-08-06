package it.ispwproject.findyourbook.dao.memory;

import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.dao.PublisherDAO;
import it.ispwproject.findyourbook.demo.DemoDataStore;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.model.Publisher;
import it.ispwproject.findyourbook.model.PublisherStats;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PublisherDAOMemory implements PublisherDAO {

    @Override
    public Publisher findById(int id) throws DAOException {
        return DemoDataStore.getInstance().getUsers().stream()
                .filter(u -> u instanceof Publisher && u.getId() == id)
                .map(Publisher.class::cast)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void publishBook(BookBean book, String publisherUsername) throws DAOException {
        Book b = new Book();
        b.setId(DemoDataStore.getInstance().nextBookId());
        b.setTitle(book.getTitle());
        b.setAuthor(book.getAuthor());
        b.setGenre(book.getGenre());
        b.setDescription(book.getDescription());
        b.setImageUrl(book.getImageUrl());
        b.setPublisherUsername(publisherUsername);
        b.setCopieLette(0);

        DemoDataStore.getInstance().getBooks().add(b);
    }

    @Override
    public List<Book> getCatalogByPublisher(String username) throws DAOException {
        return DemoDataStore.getInstance().getBooks().stream()
                .filter(b -> b.getPublisherUsername() != null && b.getPublisherUsername().equals(username))
                .toList();
    }

    @Override
    public void updateBook(BookBean book, String publisherUsername) throws DAOException {
        DemoDataStore.getInstance().getBooks().stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(book.getTitle()) && b.getPublisherUsername().equals(publisherUsername))
                .findFirst()
                .ifPresent(b -> {
                    b.setDescription(book.getDescription());
                    b.setImageUrl(book.getImageUrl());
                });
    }

    @Override
    public void deleteBook(String bookTitle, String publisherUsername) throws DAOException {
        DemoDataStore.getInstance().getBooks()
                .removeIf(b -> b.getTitle().equalsIgnoreCase(bookTitle) && b.getPublisherUsername().equals(publisherUsername));
    }

    @Override
    public PublisherStats getPublisherStatistics(String publisherUsername) throws DAOException {

        List<Book> publisherBooks = DemoDataStore.getInstance().getBooks().stream()
                .filter(b -> b.getPublisherUsername() != null && b.getPublisherUsername().equals(publisherUsername))
                .toList();

        int totalBooks = publisherBooks.size();
        int totalBooksRead = publisherBooks.stream().mapToInt(Book::getCopieLette).sum();

        Map<String, Integer> topRead = publisherBooks.stream()
                .sorted(Comparator.comparingInt(Book::getCopieLette).reversed())
                .limit(4)
                .collect(Collectors.toMap(
                        Book::getTitle,
                        Book::getCopieLette,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new // Mantiene l'ordinamento inserito!
                ));

        Map<String, Integer> byGenre = publisherBooks.stream()
                .collect(Collectors.groupingBy(
                        Book::getGenre,
                        Collectors.summingInt(Book::getCopieLette)
                ));

        return new PublisherStats(totalBooks, totalBooksRead, topRead, byGenre);
    }
}