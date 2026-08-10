package it.ispwproject.findyourbook.pattern.singleton;

import it.ispwproject.findyourbook.bean.SessionBean;
import it.ispwproject.findyourbook.enumerator.Role;
import it.ispwproject.findyourbook.model.User;


public class SessionManager {

    private User loggedUser;
    private SessionBean sessionBean;

    private boolean inactivityReminderSent = false;

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

    public boolean isReader() {
        return isLoggedIn() && loggedUser.hasRole(Role.READER);
    }

    public boolean isPublisher() {
        return isLoggedIn() && loggedUser.hasRole(Role.PUBLISHER);
    }

    public void clearSession() {
        this.loggedUser  = null;
        this.sessionBean = null;
        this.inactivityReminderSent = false;
    }

    public boolean isInactivityReminderSent() {
        return inactivityReminderSent;
    }

    public void setInactivityReminderSent(boolean inactivityReminderSent) {
        this.inactivityReminderSent = inactivityReminderSent;
    }
}