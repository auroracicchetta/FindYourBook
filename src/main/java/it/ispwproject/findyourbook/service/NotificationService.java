package it.ispwproject.findyourbook.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import it.ispwproject.findyourbook.exception.DAOException; // o NotificationException se l'hai creata
import it.ispwproject.findyourbook.exception.NotificationException;
import it.ispwproject.findyourbook.util.logger.AppLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class NotificationService {

    private static final String PROPERTIES_FILE = "src/main/resources/db.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            AppLogger.logWarning("Impossibile caricare db.properties per le notifiche email.");
        }
    }

    private static final String API_KEY    = properties.getProperty("SENDGRID_API_KEY");
    private static final String FROM_EMAIL = properties.getProperty("SENDGRID_FROM_EMAIL");

    private static final String TEMPLATE_REGISTRATION = "d-reg-template-id-fittizio";
    private static final String TEMPLATE_NEW_BOOK     = "d-book-template-id-fittizio";
    private static final String TEMPLATE_GOAL_REACHED = "d-goal-template-id-fittizio";
    private static final String TEMPLATE_READING_REMINDER = "d-reminder-template-id-fittizio";

    private static final String KEY_USER_NAME  = "userName";
    private static final String KEY_BOOK_TITLE = "bookTitle";
    private static final String KEY_ROLE       = "userRole";

    private NotificationService() {}

    // 1. Email di Conferma Registrazione
    public static void sendRegistrationConfirmation(String toEmail, String name, String role) throws NotificationException {
        if (API_KEY == null || API_KEY.isBlank()) {
            AppLogger.logInfo("[DEMO EMAIL] Registrazione confermata inviata a: " + toEmail);
            return;
        }
        try {
            Personalization p = new Personalization();
            p.addTo(new Email(toEmail));
            p.addDynamicTemplateData(KEY_USER_NAME, name);
            p.addDynamicTemplateData(KEY_ROLE, role);
            sendTemplateEmail(TEMPLATE_REGISTRATION, p);
        } catch (Exception e) {
            throw new NotificationException("Errore invio mail pubblicazione libro: " + e.getMessage(), e);
        }
    }

    // 2. Email Pubblicazione Nuovo Libro (Casa Editrice)
    public static void sendBookPublishedNotification(String toEmail, String publisherName, String bookTitle) throws NotificationException {
        if (API_KEY == null || API_KEY.isBlank()) {
            AppLogger.logInfo("[DEMO EMAIL] Libro '" + bookTitle + "' pubblicato con successo, notifica inviata a: " + toEmail);
            return;
        }
        try {
            Personalization p = new Personalization();
            p.addTo(new Email(toEmail));
            p.addDynamicTemplateData(KEY_USER_NAME, publisherName);
            p.addDynamicTemplateData(KEY_BOOK_TITLE, bookTitle);
            sendTemplateEmail(TEMPLATE_NEW_BOOK, p);
        } catch (Exception e) {
            throw new NotificationException("Errore invio mail pubblicazione libro: " + e.getMessage(), e);
        }
    }

    // 3. Email Obiettivo Lettura Raggiunto (Lettore)
    public static void sendReadingGoalReachedNotification(String toEmail, String readerName, String bookTitle) throws NotificationException {
        if (API_KEY == null || API_KEY.isBlank()) {
            AppLogger.logInfo("[DEMO EMAIL] Congratulazioni per il completamento di '" + bookTitle + "' inviata a: " + toEmail);
            return;
        }
        try {
            Personalization p = new Personalization();
            p.addTo(new Email(toEmail));
            p.addDynamicTemplateData(KEY_USER_NAME, readerName);
            p.addDynamicTemplateData(KEY_BOOK_TITLE, bookTitle);
            sendTemplateEmail(TEMPLATE_GOAL_REACHED, p);
        } catch (Exception e) {
            throw new NotificationException("Errore invio mail pubblicazione libro: " + e.getMessage(), e);
        }
    }

    public static void sendReadingReminder(String toEmail, String readerName, String bookTitle) throws NotificationException {
        if (API_KEY == null || API_KEY.isBlank()) {
            AppLogger.logInfo("[DEMO EMAIL] Promemoria 'Stai ancora leggendo?' inviato a: " + toEmail);
            return;
        }
        try {
            Personalization p = new Personalization();
            p.addTo(new Email(toEmail));
            p.addDynamicTemplateData(KEY_USER_NAME, readerName);
            p.addDynamicTemplateData(KEY_BOOK_TITLE, bookTitle);
            sendTemplateEmail(TEMPLATE_READING_REMINDER, p);
        } catch (Exception e) {
            throw new NotificationException("Errore invio mail pubblicazione libro: " + e.getMessage(), e);
        }
    }

    private static void sendTemplateEmail(String templateId, Personalization personalization) throws NotificationException {
        if (API_KEY == null || FROM_EMAIL == null) return;

        Mail mail = new Mail();
        mail.setFrom(new Email(FROM_EMAIL, "FindYourBook"));

        // Estraiamo solo l'email del destinatario per evitare l'errore SendGrid
        String toAddress = personalization.getTos().get(0).getEmail();
        Personalization cleanPersonalization = new Personalization();
        cleanPersonalization.addTo(new Email(toAddress));
        mail.addPersonalization(cleanPersonalization);

        // Prepariamo i testi in base al tipo di operazione (usando il templateId fittizio come discriminante)
        String subjectText = "Notifica ufficiale da FindYourBook";
        String headerText = "Operazione completata!";
        String bodyText = "Ti confermiamo che la tua operazione su <b>FindYourBook</b> è andata a buon fine.";

        if (TEMPLATE_REGISTRATION.equals(templateId)) {
            subjectText = "Benvenuto su FindYourBook!";
            headerText = "Registrazione completata";
            bodyText = "Ti confermiamo che la tua registrazione è andata a buon fine. Esplora il catalogo e scopri le nostre funzionalità!";
        } else if (TEMPLATE_NEW_BOOK.equals(templateId)) {
            subjectText = "Nuova pubblicazione confermata";
            headerText = "Libro pubblicato con successo!";
            bodyText = "Il tuo nuovo libro è stato appena pubblicato e aggiunto al catalogo ufficiale di FindYourBook. Ottimo lavoro!";
        } else if (TEMPLATE_GOAL_REACHED.equals(templateId)) {
            subjectText = "Obiettivo di lettura raggiunto!";
            headerText = "Congratulazioni!";
            bodyText = "Hai appena completato la lettura di un nuovo libro! Complimenti per aver raggiunto questo nuovo traguardo. Il tuo contatore è stato aggiornato.";
        }
        else if (TEMPLATE_READING_REMINDER.equals(templateId)) {
            subjectText = "Stai ancora leggendo?";
            headerText = "È passato un po' di tempo...";
            bodyText = "Abbiamo notato che hai un libro bloccato nella tua libreria nella sezione 'In Lettura' da più di 30 giorni. Non mollare, continua a leggere! Se l'hai già finito, ricordati di aggiornare lo stato in 'Letto'.";
        }

        // Il template HTML grafico (Sfondo beige EFE8D8)
        String htmlBody = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 8px rgba(0,0,0,0.05);\">" +
                "<div style=\"background-color: #EFE8D8; color: #2c3e50; padding: 20px; text-align: center;\">" +
                "<h1 style=\"margin: 0;\">FindYourBook</h1>" +
                "</div>" +
                "<div style=\"padding: 30px; color: #333333; line-height: 1.6; font-size: 16px;\">" +
                "<h2 style=\"color: #2c3e50;\">" + headerText + "</h2>" +
                "<p>Ciao,</p>" +
                "<p>" + bodyText + "</p>" +
                "<p>Continua ad esplorare il catalogo e a gestire la tua libreria personale.</p>" +
                "<br>" +
                "<p>Buona lettura,<br><i>Il Team di FindYourBook</i></p>" +
                "</div>" +
                "<div style=\"background-color: #f8f9fa; padding: 15px; text-align: center; font-size: 12px; color: #777777; border-top: 1px solid #e0e0e0;\">" +
                "<p>© 2026 FindYourBook. Tutti i diritti riservati.</p>" +
                "</div>" +
                "</div>";

        com.sendgrid.helpers.mail.objects.Content content = new com.sendgrid.helpers.mail.objects.Content(
                "text/html",
                htmlBody
        );

        mail.addContent(content);
        mail.setSubject(subjectText);

        SendGrid sg = new SendGrid(API_KEY);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {

            } else {
                throw new NotificationException("Errore SendGrid (Status " + response.getStatusCode() + "): " + response.getBody());
            }

        } catch (Exception e) {
            throw new NotificationException("Errore di rete invio SendGrid: " + e.getMessage(), e);
        }
    }
}