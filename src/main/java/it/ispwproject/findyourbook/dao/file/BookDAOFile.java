package it.ispwproject.findyourbook.dao.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.ispwproject.findyourbook.dao.BookDAO;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.util.logger.AppLogger;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookDAOFile implements BookDAO {

    private static final String FILE_PATH = "books.json";
    private final Gson gson;

    public BookDAOFile() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
        // Nessuna cache caricata una volta sola nel costruttore: ogni metodo
        // ricarica fresco da books.json (stesso motivo di ReaderDAOFile) cosi'
        // un controller che tiene questa istanza a lungo (es. BookController in
        // una schermata GUI) vede sempre lo stato più recente su disco.
    }

    @Override
    public List<Book> findByGenre(String genre) throws DAOException {
        if (genre == null) return new ArrayList<>();
        List<Book> cache = loadFromFile();
        return cache.stream()
                .filter(b -> b.getGenre() != null && b.getGenre().equalsIgnoreCase(genre))
                .toList();
    }

    @Override
    public List<Book> searchByQuery(String query) throws DAOException {
        if (query == null || query.trim().isEmpty()) return new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        List<Book> cache = loadFromFile();

        return cache.stream()
                .filter(b -> (b.getTitle() != null && b.getTitle().toLowerCase().contains(lowerQuery)) ||
                        (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(lowerQuery)))
                .toList();
    }

    @Override
    public void save(Book book) throws DAOException {
        List<Book> cache = loadFromFile();
        if (book.getId() == 0) {
            book.setId(cache.size() + 1);
        }
        cache.add(book);
        saveToFile(cache);
    }

    private List<Book> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Book>>() {}.getType();
            List<Book> loaded = gson.fromJson(reader, listType);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void saveToFile(List<Book> cache) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(cache, writer);
        } catch (IOException e) {
            AppLogger.logError("Errore salvataggio books su file: " + e.getMessage());
        }
    }
}