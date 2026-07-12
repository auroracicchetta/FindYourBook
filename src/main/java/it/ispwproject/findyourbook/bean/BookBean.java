package it.ispwproject.findyourbook.bean;

import it.ispwproject.findyourbook.enumerator.ReadingStatus;

import java.time.LocalDate;

public class BookBean {
    private String title;
    private String author;
    private String genre;
    private String imageUrl;
    private int rating;
    private String description;
    private ReadingStatus status;
    private int copieVendute;
    private LocalDate readingStartDate;

    public BookBean() {}

    public BookBean(String title, String author, String genre, String imageUrl, String description) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public BookBean(String title, String author, String genre, String imageUrl) {
        this(title, author, genre, imageUrl, "Trama non disponibile.");
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ReadingStatus getStatus() { return status; }
    public void setStatus(ReadingStatus status) { this.status = status; }

    public int getCopieVendute() {return copieVendute;}
    public void setCopieVendute(int copieVendute) {this.copieVendute = copieVendute;}

    public LocalDate getReadingStartDate() { return readingStartDate; }
    public void setReadingStartDate(LocalDate readingStartDate) { this.readingStartDate = readingStartDate; }
}
