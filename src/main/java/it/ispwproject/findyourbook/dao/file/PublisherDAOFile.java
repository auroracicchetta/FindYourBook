package it.ispwproject.findyourbook.dao.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.dao.PublisherDAO;
import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.model.Publisher;
import it.ispwproject.findyourbook.model.PublisherStats;
import it.ispwproject.findyourbook.util.logger.AppLogger;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Niente cache d'istanza: books.json e' "posseduto" da BookDAOFile (la ricerca
// generale libri), qui lo trattiamo come le colleghe trattano il file di
// un'altra entita' (es. updateSlotAvailability/updateEventTickets) - si
// rilegge fresco a ogni chiamata, si applica la modifica, si riscrive subito.
// Cosi' due DAO diversi non si contendono piu' la stessa cache in memoria.
public class PublisherDAOFile implements PublisherDAO {

    private static final String FILE_PATH = "books.json";
    private static final String FAVORITES_FILE_PATH = "favorites.json";
    private final Gson gson;

    public PublisherDAOFile() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
    }

    @Override
    public Publisher findById(int id) throws DAOException {
        return null;
    }

    @Override
    public PublisherStats getPublisherStatistics(String publisherUsername) throws DAOException {
        List<Book> catalog = loadFromFile().stream()
                .filter(b -> publisherUsername.equalsIgnoreCase(b.getPublisherUsername()))
                .toList();

        int totalBooksPublished = catalog.size();

        // In modalita' Database, ogni volta che un lettore segna un libro come
        // "Letto" viene incrementato il contatore copie_lette (ReaderDAODB).
        // In modalita' File non esiste una colonna persistita da incrementare:
        // il dato equivalente si ricava contando, ad ogni richiesta, quante
        // librerie personali (favorites.json) hanno quel titolo in stato READ.
        Map<String, Integer> readCountByTitle = countReadsByTitle();

        List<Book> sortedByReads = catalog.stream()
                .sorted(Comparator.comparingInt(
                        (Book b) -> readCountByTitle.getOrDefault(b.getTitle(), 0)).reversed())
                .toList();

        int totalBooksRead = 0;
        Map<String, Integer> byGenre = new LinkedHashMap<>();
        for (Book b : sortedByReads) {
            int reads = readCountByTitle.getOrDefault(b.getTitle(), 0);
            totalBooksRead += reads;
            byGenre.merge(b.getGenre(), reads, Integer::sum);
        }

        Map<String, Integer> topRead = new LinkedHashMap<>();
        sortedByReads.stream()
                .limit(4)
                .forEach(b -> topRead.put(b.getTitle(), readCountByTitle.getOrDefault(b.getTitle(), 0)));

        return new PublisherStats(totalBooksPublished, totalBooksRead, topRead, byGenre);
    }

    @Override
    public void publishBook(BookBean bookBean, String publisherUsername) throws DAOException {
        List<Book> books = loadFromFile();

        Book book = new Book();
        int newId = books.stream().mapToInt(Book::getId).max().orElse(0) + 1;
        book.setId(newId);

        book.setTitle(bookBean.getTitle());
        book.setAuthor(bookBean.getAuthor());
        book.setGenre(bookBean.getGenre());
        book.setDescription(bookBean.getDescription());
        book.setImageUrl(bookBean.getImageUrl());
        book.setRating(0);
        book.setPublisherUsername(publisherUsername);

        books.add(book);
        saveToFile(books);
    }

    @Override
    public List<Book> getCatalogByPublisher(String username) throws DAOException {
        return loadFromFile().stream()
                .filter(b -> username.equalsIgnoreCase(b.getPublisherUsername()))
                .toList();
    }

    @Override
    public void updateBook(BookBean bookBean, String publisherUsername) throws DAOException {
        List<Book> books = loadFromFile();
        boolean updated = false;
        for (Book b : books) {
            if (b.getTitle().equalsIgnoreCase(bookBean.getTitle())
                    && publisherUsername.equalsIgnoreCase(b.getPublisherUsername())) {
                b.setAuthor(bookBean.getAuthor());
                b.setGenre(bookBean.getGenre());
                b.setDescription(bookBean.getDescription());
                b.setImageUrl(bookBean.getImageUrl());
                updated = true;
                break;
            }
        }

        if (updated) {
            saveToFile(books);
        } else {
            throw new DAOException("Libro non trovato nel catalogo file.");
        }
    }

    @Override
    public void deleteBook(String bookTitle, String publisherUsername) throws DAOException {
        List<Book> books = loadFromFile();
        boolean removed = books.removeIf(b -> b.getTitle().equalsIgnoreCase(bookTitle)
                && publisherUsername.equalsIgnoreCase(b.getPublisherUsername()));

        if (removed) {
            saveToFile(books);
        } else {
            throw new DAOException("Libro non trovato per l'eliminazione.");
        }
    }

    private List<Book> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Book>>() {}.getType();
            List<Book> loaded = gson.fromJson(reader, listType);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            AppLogger.logError("Errore caricamento da books.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveToFile(List<Book> books) throws DAOException {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(books, writer);
        } catch (IOException e) {
            throw new DAOException("Errore durante il salvataggio su file JSON: " + e.getMessage());
        }
    }

    private Map<String, Integer> countReadsByTitle() {
        Map<String, Integer> counts = new HashMap<>();
        File file = new File(FAVORITES_FILE_PATH);
        if (!file.exists()) return counts;

        try (Reader reader = new FileReader(file)) {
            Type mapType = new TypeToken<HashMap<String, List<Book>>>() {}.getType();
            Map<String, List<Book>> favorites = gson.fromJson(reader, mapType);
            if (favorites == null) return counts;

            for (List<Book> userBooks : favorites.values()) {
                if (userBooks == null) continue;
                for (Book b : userBooks) {
                    if (b.getStatus() == ReadingStatus.READ) {
                        counts.merge(b.getTitle(), 1, Integer::sum);
                    }
                }
            }
        } catch (IOException e) {
            AppLogger.logError("Errore caricamento da favorites.json per le statistiche: " + e.getMessage());
        }
        return counts;
    }
}