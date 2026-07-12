package it.ispwproject.findyourbook.model;

import it.ispwproject.findyourbook.enumerator.ReadingStatus;
import it.ispwproject.findyourbook.pattern.observer.Observable;

import java.time.LocalDate;

public class Book extends Observable {
    private int id;
    private String title;
    private String author;
    private String genre;
    private String imageUrl;
    private String description;
    private int rating;
    private ReadingStatus status;
    private String publisherUsername;
    private int copieVendute;
    private LocalDate readingStartDate;

    public Book() {}

    public Book(int id, String title, String author, String genre, String imageUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.imageUrl = imageUrl;
    }


    public void markAsRead() {
        this.status = ReadingStatus.READ;
        notifyObservers();
    }

    public void markAsPublished() {
        notifyObservers();
    }

    public void triggerReminder() {
        notifyObservers();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public ReadingStatus getStatus() { return status; }
    public void setStatus(ReadingStatus status) { this.status = status; }
    public void setPublisherUsername(String publisherUsername) {
        this.publisherUsername = publisherUsername;
    }
    public String getPublisherUsername() {
        return publisherUsername;
    }

    public int getCopieVendute() { return copieVendute; }
    public void setCopieVendute(int copieVendute) { this.copieVendute = copieVendute; }

    public LocalDate getReadingStartDate() { return readingStartDate; }
    public void setReadingStartDate(LocalDate readingStartDate) { this.readingStartDate = readingStartDate; }
}