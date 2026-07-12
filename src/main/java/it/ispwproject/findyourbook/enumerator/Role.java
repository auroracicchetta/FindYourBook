package it.ispwproject.findyourbook.enumerator;

public enum Role {
    READER,
    PUBLISHER,
    ADMIN;

    public static Role fromString(String role) {
        return switch (role.toUpperCase()) {
            case "READER" -> READER;
            case "PUBLISHER" -> PUBLISHER;
            case "ADMIN" -> ADMIN;
            default -> throw new IllegalArgumentException(
                    "Ruolo non valido: " + role);
        };
    }
}