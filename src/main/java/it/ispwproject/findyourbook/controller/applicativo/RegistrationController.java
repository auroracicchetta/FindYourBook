package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.bean.RegistrationBean;
import it.ispwproject.findyourbook.dao.DAOFactory;
import it.ispwproject.findyourbook.dao.RegistrationDAO;
import it.ispwproject.findyourbook.enumerator.Role;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.exception.LoginException;
import it.ispwproject.findyourbook.exception.RegistrationException;
import it.ispwproject.findyourbook.model.Publisher;
import it.ispwproject.findyourbook.model.Reader;
import it.ispwproject.findyourbook.model.User;
import it.ispwproject.findyourbook.util.PasswordUtils;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class RegistrationController {

    private final RegistrationDAO registrationDAO;

    public RegistrationController() {
        this.registrationDAO = DAOFactory.getRegistrationDAO();
    }

    public void register(RegistrationBean bean) throws DAOException, RegistrationException {

        validateBean(bean);

        if (registrationDAO.usernameExists(bean.getUsername())) {
            throw new RegistrationException("Username già in uso. Scegline un altro.");
        }

        String hashedPassword;
        try {
            hashedPassword = PasswordUtils.hash(bean.getPassword());
        } catch (LoginException e) {
            throw new RegistrationException("Errore durante la codifica della password.", e);
        }

        User user;
        if (bean.getRole() == Role.CASA_EDITRICE) {
            // Aggiunto bean.getDataNascita() prima della descrizione
            user = new Publisher(0, bean.getName(), bean.getSurname(), bean.getUsername(), hashedPassword, bean.getDataNascita(), bean.getDescrizione());
        } else {
            // Aggiunto bean.getDataNascita() alla fine
            user = new Reader(0, bean.getName(), bean.getSurname(), bean.getUsername(), hashedPassword, bean.getDataNascita());
        }

        registrationDAO.save(user);
    }

    private void validateBean(RegistrationBean bean) throws RegistrationException {
        if (bean == null) throw new RegistrationException("Dati di registrazione non validi.");

        validateRequiredField(bean.getName(), "Il nome è obbligatorio.");
        validateRequiredField(bean.getSurname(), "Il cognome è obbligatorio.");
        validateRequiredField(bean.getUsername(), "L'username è obbligatorio.");
        validatePassword(bean);
        validateRole(bean);

        if (bean.getRole() == Role.LETTORE) {
            validateAge(bean.getDataNascita());
        }

        if (bean.getRole() == Role.CASA_EDITRICE) {
            validateRequiredField(bean.getDescrizione(), "La descrizione aziendale è obbligatoria per le Case Editrici.");
        }
    }

    private void validateRequiredField(String value, String message) throws RegistrationException {
        if (value == null || value.isBlank()) {
            throw new RegistrationException(message);
        }
    }

    private void validatePassword(RegistrationBean bean) throws RegistrationException {
        if (bean.getPassword() == null || bean.getPassword().length() < 8) {
            throw new RegistrationException("La password deve essere di almeno 8 caratteri.");
        }
        if (bean.getPassword().chars().noneMatch(Character::isUpperCase)) {
            throw new RegistrationException("La password deve contenere almeno una lettera maiuscola.");
        }
        if (bean.getPassword().chars().noneMatch(Character::isDigit)) {
            throw new RegistrationException("La password deve contenere almeno un numero.");
        }
        if (!bean.getPassword().equals(bean.getConfirmPassword())) {
            throw new RegistrationException("Le password non coincidono.");
        }
    }

    private void validateRole(RegistrationBean bean) throws RegistrationException {
        if (bean.getRole() == null) {
            throw new RegistrationException("Seleziona un ruolo.");
        }
    }

    private void validateAge(LocalDate birthDate) throws RegistrationException {
        if (birthDate == null) {
            throw new RegistrationException("La data di nascita è obbligatoria.");
        }

        // Calcola l'età rispetto alla data di oggi
        if (Period.between(birthDate, LocalDate.now(ZoneId.systemDefault())).getYears() < 14) {
            throw new RegistrationException("Devi avere almeno 14 anni per registrarti.");
        }
    }

}