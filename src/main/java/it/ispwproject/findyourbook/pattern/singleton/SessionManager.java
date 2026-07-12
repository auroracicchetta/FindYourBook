package it.ispwproject.findyourbook.pattern.singleton;

import it.ispwproject.findyourbook.bean.SessionBean;
import it.ispwproject.findyourbook.enumerator.Role;
import it.ispwproject.findyourbook.model.User;


public class SessionManager {

    private User loggedUser;
    private SessionBean sessionBean;

    private SessionManager() {}

    private static class Holder {
        private static final SessionManager INSTANCE = new SessionManager();
    }

    public static SessionManager getInstance() {
        return Holder.INSTANCE;
    }

    public void setLoggedUser(User user) {
        this.loggedUser = user;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public boolean isLoggedIn() {
        return loggedUser != null;
    }

    // Metodi di controllo ruolo adattati al tuo progetto
    public boolean isLettore() {
        return isLoggedIn() && loggedUser.getRole() == Role.READER;
    }

    public boolean isCasaEditrice() {
        return isLoggedIn() && loggedUser.getRole() == Role.PUBLISHER;
    }

    public void clearSession() {
        this.loggedUser  = null;
        this.sessionBean = null;
    }
}