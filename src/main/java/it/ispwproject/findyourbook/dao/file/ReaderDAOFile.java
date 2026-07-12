package it.ispwproject.findyourbook.dao.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.ispwproject.findyourbook.dao.ReaderDAO;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.Book;
import it.ispwproject.findyourbook.model.Reader;
import it.ispwproject.findyourbook.util.logger.AppLogger;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReaderDAOFile implements ReaderDAO {

    private static final String FILE_PATH = "favorites.json";
    private final Gson gson;
    private final Map<String, List<Book>> favoritesMap; // Username -> Lista di Libri
    private final List<Reader> readersCache = new ArrayList<>(); // Cache interna per i reader

    public ReaderDAOFile() {
        System.out.println("-----> [DEBUG] STO CREANDO I FILE JSON! <-----");
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
        this.favoritesMap = loadFromFile();
    }

    @Override
    public Reader findById(int id) throws DAOException {
        return readersCache.stream()
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Book> getBooksByStatus(String username, String status) throws DAOException {
        List<Book> userBooks = favoritesMap.getOrDefault(username, new ArrayList<>());
        return userBooks.stream()
                .filter(b -> b.getStatus() != null && b.getStatus().name().equalsIgnoreCase(status))
                .toList();
    }

    @Override
    public void addFavoriteBook(String username, Book book, String status) throws DAOException {
        favoritesMap.computeIfAbsent(username, k -> new ArrayList<>()).add(book);
        saveToFile();
    }

    @Override
    public void removeFavoriteBook(String username, String bookTitle) throws DAOException {
        List<Book> userBooks = favoritesMap.get(username);
        if (userBooks != null) {
            userBooks.removeIf(b -> b.getTitle().equalsIgnoreCase(bookTitle));
            saveToFile();
        }
    }

    @Override
    public void updateRating(String username, String bookTitle, int rating) throws DAOException {
        List<Book> userBooks = favoritesMap.get(username);
        if (userBooks != null) {
            userBooks.stream()
                    .filter(b -> b.getTitle().equalsIgnoreCase(bookTitle))
                    .findFirst()
                    .ifPresent(b -> b.setRating(rating));
            saveToFile();
        }
    }

    private Map<String, List<Book>> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        // Usiamo esplicitamente java.io.Reader per evitare conflitti con il model Reader!
        try (java.io.Reader fileReader = new FileReader(file)) {
            Type mapType = new TypeToken<HashMap<String, List<Book>>>() {}.getType();
            Map<String, List<Book>> loaded = gson.fromJson(fileReader, mapType);
            return loaded != null ? loaded : new HashMap<>();
        } catch (IOException e) {
            AppLogger.logError("Errore caricamento favorites da file: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(favoritesMap, writer);
        } catch (IOException e) {
            AppLogger.logError("Errore salvataggio favorites su file: " + e.getMessage());
        }
    }
}