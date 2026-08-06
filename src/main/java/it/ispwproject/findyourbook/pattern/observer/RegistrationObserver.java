package it.ispwproject.findyourbook.pattern.observer;

import it.ispwproject.findyourbook.bean.RegistrationBean;
import it.ispwproject.findyourbook.model.User;
import it.ispwproject.findyourbook.service.NotificationService;
import it.ispwproject.findyourbook.exception.NotificationException;
import it.ispwproject.findyourbook.util.logger.AppLogger;

// Il Subject e' User (non Book): la registrazione riguarda l'utente
// appena creato. Tiene l'Entity, come le colleghe, e costruisce il Bean
// per la mail solo al momento della notifica.
public class RegistrationObserver implements Observer {
    private final User user;

    public RegistrationObserver(User user) {
        this.user = user;
    }

    @Override
    public void update() {
        try {
            NotificationService.sendRegistrationConfirmation(buildBean());
        } catch (NotificationException e) {
            AppLogger.logWarning("Notifica di registrazione non inviata: " + e.getMessage());
        }
    }

    private RegistrationBean buildBean() {
        RegistrationBean bean = new RegistrationBean();
        bean.setEmail(user.getEmail());
        bean.setName(user.getName());
        bean.setRole(user.getRole());
        return bean;
    }
}