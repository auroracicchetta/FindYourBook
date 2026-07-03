package it.ispwproject.findyourbook.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class BookService {

    private static final String API_KEY;

    static {
        Properties properties = new Properties();
        // Puntiamo allo stesso file che usa il collega
        try (InputStream input = new FileInputStream("src/main/resources/db.properties")) {
            properties.load(input);
            API_KEY = properties.getProperty("GOOGLE_BOOKS_API_KEY");
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Errore: impossibile leggere db.properties");
        }
    }

    // Risolto il code smell: usiamo le eccezioni specifiche di rete invece di "Exception"
    public String fetchBooksJson(String genre) throws IOException, InterruptedException {
        // Codifichiamo il genere per sicurezza (es. "Fantasy" diventa "Fantasy")
        String encodedGenre = URLEncoder.encode(genre, StandardCharsets.UTF_8);

        // Costruiamo l'URL pulito
        String urlString = "https://www.googleapis.com/books/v1/volumes?q=subject:"
                + encodedGenre
                + "&key=" + API_KEY;

        HttpClient client = HttpClient.newHttpClient();
        // Usiamo URI.create(urlString) che è più tollerante
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            // Risolto code smell: lanciamo un'eccezione specifica
            throw new IOException("Errore API: " + response.statusCode());
        }

        return response.body();
    }
}