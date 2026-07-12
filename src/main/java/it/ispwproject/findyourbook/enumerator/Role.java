package it.ispwproject.findyourbook.enumerator;

public enum Role {
    READER,
    PUBLISHER;

    public static Role fromString(String role) {
        return switch (role.toUpperCase()) {
            case "READER" -> READER;
            case "PUBLISHER" -> PUBLISHER;
            default -> throw new IllegalArgumentException(
                    "Ruolo non valido: " + role);
        };
    }
}