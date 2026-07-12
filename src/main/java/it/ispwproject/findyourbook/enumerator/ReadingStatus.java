package it.ispwproject.findyourbook.enumerator;

public enum ReadingStatus {
    TO_READ("Da leggere"),
    READING("In lettura"),
    READ("Letto");

    private final String displayName;

    ReadingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}