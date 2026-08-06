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
    private final List<Reader> readersCache = new ArrayList<>(); // Cache interna per i reader

    public ReaderDAOFile() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
        // Niente piu' favoritesMap caricata una volta sola nel costruttore: ogni
        // metodo pubblico ricarica fresco da favorites.json (vedi loadFromFile()),
        // stesso pattern gia' applicato a PublisherDAOFile. Cosi' anche se un
        // controller (es. BookController/UserLibraryController in una schermata
        // GUI a lunga vita) tiene la stessa istanza per piu' azioni, ogni singola
        // lettura/scrittura vede sempre lo stato più recente su disco, invece di
        // restare bloccata su una copia in memoria caricata all'inizio.
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
        Map<String, List<Book>> favoritesMap = loadFromFile();
        List<Book> userBooks = favoritesMap.getOrDefault(username, new ArrayList<>());
        return userBooks.stream()
                .filter(b -> b.getStatus() != null && b.getStatus().name().equalsIgnoreCase(status))
                .toList();
    }

    @Override
    public void addFavoriteBook(String username, Book book, String status) throws DAOException {
        // Rimuove l'eventuale voce gia' presente per lo stesso libro prima di
        // riaggiungerlo con il nuovo stato, come fa ReaderDAOMemory: altrimenti
        // un cambio di stato (es. TO_READ -> READING) duplica il libro invece
        // di spostarlo. Rilegge da file a ogni chiamata (vedi commento nel
        // costruttore) cosi' non sovrascrive mai dati scritti nel frattempo da
        // un'altra istanza dello stesso DAO.
        Map<String, List<Book>> favoritesMap = loadFromFile();
        List<Book> userBooks = favoritesMap.computeIfAbsent(username, k -> new ArrayList<>());
        userBooks.removeIf(b -> b.getTitle().equalsIgnoreCase(book.getTitle()));
        userBooks.add(book);
        saveToFile(favoritesMap);
    }

    @Override
    public void removeFavoriteBook(String username, String bookTitle) throws DAOException {
        Map<String, List<Book>> favoritesMap = loadFromFile();
        List<Book> userBooks = favoritesMap.get(username);
        if (userBooks != null) {
            userBooks.removeIf(b -> b.getTitle().equalsIgnoreCase(bookTitle));
            saveToFile(favoritesMap);
        }
    }

    @Override
    public void updateRating(String username, String bookTitle, int rating) throws DAOException {
        Map<String, List<Book>> favoritesMap = loadFromFile();
        List<Book> userBooks = favoritesMap.get(username);
        if (userBooks != null) {
            userBooks.stream()
                    .filter(b -> b.getTitle().equalsIgnoreCase(bookTitle))
                    .findFirst()
                    .ifPresent(b -> b.setRating(rating));
            saveToFile(favoritesMap);
        }
    }

    private Map<String, List<Book>> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (java.io.Reader fileReader = new FileReader(file)) {
            Type mapType = new TypeToken<HashMap<String, List<Book>>>() {}.getType();
            Map<String, List<Book>> loaded = gson.fromJson(fileReader, mapType);
            return loaded != null ? loaded : new HashMap<>();
        } catch (IOException e) {
            AppLogger.logError("Errore caricamento favorites da file: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private void saveToFile(Map<String, List<Book>> favoritesMap) {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(favoritesMap, writer);
        } catch (IOException e) {
            AppLogger.logError("Errore salvataggio favorites su file: " + e.getMessage());
        }
    }
}