package it.ispwproject.findyourbook.model;

import java.util.Map;

public class PublisherStats {
    private int totalBooksPublished;
    private int totalCopiesSold;
    private Map<String, Integer> topSellingBooks;
    private Map<String, Integer> salesByGenre;

    public PublisherStats(int totalBooksPublished, int totalCopiesSold,
                          Map<String, Integer> topSellingBooks, Map<String, Integer> salesByGenre) {
        this.totalBooksPublished = totalBooksPublished;
        this.totalCopiesSold     = totalCopiesSold;
        this.topSellingBooks     = topSellingBooks;
        this.salesByGenre        = salesByGenre;
    }

    public int getTotalBooksPublished() { return totalBooksPublished; }
    public int getTotalCopiesSold()     { return totalCopiesSold; }
    public Map<String, Integer> getTopSellingBooks() { return topSellingBooks; }
    public Map<String, Integer> getSalesByGenre()    { return salesByGenre; }
}