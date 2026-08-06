package it.ispwproject.findyourbook.pattern.state;

import it.ispwproject.findyourbook.controller.cli.InitialCLI;
import java.util.ArrayDeque;
import java.util.Deque;

public class CLIStateMachineImpl implements CLIStateMachine {

    private AbstractCLIState currentState;
    private final Deque<AbstractCLIState> stateHistory = new ArrayDeque<>();

    public CLIStateMachineImpl() {
        this.currentState = new InitialCLI();
    }

    @Override
    public void start() {
        currentState = new InitialCLI();
        currentState.entry(this);
        goNext();
    }

    @Override
    public void goNext() {
        if (currentState != null) {
            currentState.action(this);
        }
    }

    @Override
    public void goBack() {
        if (!stateHistory.isEmpty()) {
            currentState.exit(this);
            currentState = stateHistory.pop();
            currentState.entry(this);
            goNext();
        }
    }

    @Override
    public void transition(AbstractCLIState nextState) {
        currentState.exit(this);
        if (currentState != null) {
            stateHistory.push(currentState);
        }
        currentState = nextState;
        currentState.entry(this);
        goNext();
    }

    @Override
    public void redirect(AbstractCLIState nextState) {
        // Come transition(), ma SENZA spingere lo stato corrente sullo
        // stateHistory: da usare quando si completa un flusso (registrazione
        // riuscita -> Login, login riuscito -> Dashboard) e la schermata
        // appena conclusa non deve restare raggiungibile con goBack().
        if (currentState != null) {
            currentState.exit(this);
        }
        currentState = nextState;
        currentState.entry(this);
        goNext();
    }

    @Override
    public AbstractCLIState getState() {
        return currentState;
    }

    @Override
    public void setState(AbstractCLIState state) {
        this.currentState = state;
    }
}