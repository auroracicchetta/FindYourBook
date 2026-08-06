package it.ispwproject.findyourbook.bean;

import java.util.Map;

public class PublisherStatsBean {

    private int totalBooksPublished;
    private int totalBooksRead;
    private Map<String, Integer> topReadBooks;
    private Map<String, Integer> readsByGenre;

    public PublisherStatsBean(int totalBooksPublished, int totalBooksRead,
                              Map<String, Integer> topReadBooks, Map<String, Integer> readsByGenre) {
        this.totalBooksPublished = totalBooksPublished;
        this.totalBooksRead     = totalBooksRead;
        this.topReadBooks     = topReadBooks;
        this.readsByGenre        = readsByGenre;
    }

    public int getTotalBooksPublished() { return totalBooksPublished; }
    public int getTotalBooksRead()     { return totalBooksRead; }
    public Map<String, Integer> getTopReadBooks() { return topReadBooks; }
    public Map<String, Integer> getReadsByGenre()    { return readsByGenre; }
}