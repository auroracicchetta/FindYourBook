package it.ispwproject.findyourbook.controller.cli;

import it.ispwproject.findyourbook.dao.ConnectionFactory;
import it.ispwproject.findyourbook.pattern.singleton.SessionManager;
import it.ispwproject.findyourbook.pattern.state.AbstractCLIState;


/**
 * Classe base comune alle dashboard CLI con sessione utente attiva
 * (ReaderDashboardCLI, PublisherDashboardCLI). Raccoglie il comportamento di
 * logout, identico in entrambe, che NON appartiene al meccanismo generico del
 * pattern State (AbstractCLIState resta puramente pattern-mechanics, senza
 * conoscenza di sessione/DB) ma e' specifico di chi ha una sessione da
 * chiudere. Ogni ConcreteState resta comunque responsabile del redirect
 * verso lo stato successivo dopo aver chiamato logout().
 */
public abstract class DashboardCLI extends AbstractCLIState {

    protected void logout() {
        SessionManager.getInstance().clearSession();
        ConnectionFactory.clearRole();
    }
}