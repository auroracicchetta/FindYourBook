package it.ispwproject.findyourbook.dao.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.ispwproject.findyourbook.bean.BookBean;
import it.ispwproject.findyourbook.dao.PublisherDAO;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.model.Publisher;
import it.ispwproject.findyourbook.model.PublisherStats;
import it.ispwproject.findyourbook.util.logger.AppLogger;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PublisherDAOFile implements PublisherDAO {


    private static final String FILE_PATH = "books.json";
    private final Gson gson;
    private final List<Book> cache;

    public PublisherDAOFile() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
        this.cache = loadFromFile();
    }

    @Override
    public Publisher findById(int id) throws DAOException {
        return null;
    }

    @Override
    public PublisherStats getPublisherStatistics(String publisherUsername) throws DAOException {
        return new PublisherStats(0, 0, new HashMap<>(), new HashMap<>());
    }

    @Override
    public void publishBook(BookBean bookBean, String publisherUsername) throws DAOException {
        Book book = new Book();


        int newId = cache.stream().mapToInt(Book::getId).max().orElse(0) + 1;
        book.setId(newId);


        book.setTitle(bookBean.getTitle());
        book.setAuthor(bookBean.getAuthor());
        book.setGenre(bookBean.getGenre());
        book.setDescription(bookBean.getDescription());
        book.setImageUrl(bookBean.getImageUrl());
        book.setRating(0);


        cache.add(book);
        saveToFile();
    }

    @Override
    public List<Book> getCatalogByPublisher(String username) throws DAOException {
        return new ArrayList<>(cache);
    }

    @Override
    public void updateBook(BookBean bookBean, String publisherUsername) throws DAOException {
        boolean updated = false;
        for (Book b : cache) {
            if (b.getTitle().equalsIgnoreCase(bookBean.getTitle())) {
                b.setAuthor(bookBean.getAuthor());
                b.setGenre(bookBean.getGenre());
                b.setDescription(bookBean.getDescription());
                b.setImageUrl(bookBean.getImageUrl());
                updated = true;
                break;
            }
        }

        if (updated) {
            saveToFile();
        } else {
            throw new DAOException("Libro non trovato nel catalogo file.");
        }
    }

    @Override
    public void deleteBook(String bookTitle, String publisherUsername) throws DAOException {
        boolean removed = cache.removeIf(b -> b.getTitle().equalsIgnoreCase(bookTitle));

        if (removed) {
            saveToFile();
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

    private void saveToFile() throws DAOException {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(cache, writer);
        } catch (IOException e) {
            throw new DAOException("Errore durante il salvataggio su file JSON: " + e.getMessage());
        }
    }
}