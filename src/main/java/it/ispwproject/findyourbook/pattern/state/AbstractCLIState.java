package it.ispwproject.findyourbook.pattern.state;

/**
 * AbstractState del pattern GoF State.
 * Ogni schermata CLI estende questa classe e implementa action().
 * Le transizioni avvengono chiamando goNext() o goBack() dall'interno di action().
 */
public abstract class AbstractCLIState {

    protected AbstractCLIState() {}

    /** Comportamento dello stato corrente — implementato da ogni ConcreteState. */
    public abstract void action(CLIStateMachine context);

    /** Azione eseguita all'ingresso nello stato. */
    public void entry(CLIStateMachine context) {}

    /** Azione eseguita all'uscita nello stato. */
    public void exit(CLIStateMachine context) {}

    /** Transisce allo stato successivo. */
    public void goNext(CLIStateMachine context, AbstractCLIState nextState) {
        context.transition(nextState);
    }

    /** Torna allo stato precedente. */
    public void goBack(CLIStateMachine context) {
        context.goBack();
    }

    /**
     * Ripete lo stato corrente (es. scelta non valida, nuovo tentativo)
     * SENZA alterare lo storico di navigazione. A differenza di
     * goNext(context, this), che tramite transition() spingerebbe una copia
     * dello stato corrente sullo stateHistory ad ogni ripetizione, repeat()
     * richiama solo action() sullo stato già attivo. Questo evita che lo
     * stack di goBack() si "gonfi" di duplicati e che il tasto 0 richieda
     * un numero di pressioni crescente per tornare davvero indietro.
     */
    public void repeat(CLIStateMachine context) {
        context.goNext();
    }

    /**
     * Passa a un nuovo stato SENZA lasciare quello corrente nello storico di
     * navigazione. Da usare quando la transizione rappresenta il completamento
     * di un flusso (es. registrazione riuscita -> Login, login riuscito ->
     * Dashboard), non una schermata da cui l'utente deve poter tornare con
     * goBack(). A differenza di goNext(), lo stato di partenza NON deve più
     * essere raggiungibile premendo 0 più avanti.
     */
    public void redirect(CLIStateMachine context, AbstractCLIState nextState) {
        context.redirect(nextState);
    }

    public boolean isBackChoice(String input) {
        return input.equals("0");
    }


}