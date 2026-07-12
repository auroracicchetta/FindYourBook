package it.ispwproject.findyourbook.pattern.observer;

import it.ispwproject.findyourbook.service.NotificationService;
import it.ispwproject.findyourbook.exception.NotificationException;
import it.ispwproject.findyourbook.util.logger.AppLogger;

public class RegistrationObserver implements Observer {
    private final String email;
    private final String username;
    private final String role;

    public RegistrationObserver(String email, String username, String role) {
        this.email = email;
        this.username = username;
        this.role = role;
    }

    @Override
    public void update() {
        try {
            NotificationService.sendRegistrationConfirmation(email, username, role);
        } catch (NotificationException e) {
            AppLogger.logWarning("Notifica di registrazione non inviata: " + e.getMessage());
        }
    }
}