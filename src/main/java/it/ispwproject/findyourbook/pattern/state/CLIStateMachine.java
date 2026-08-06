package it.ispwproject.findyourbook.pattern.state;

public interface CLIStateMachine {
    void start();
    void goNext();
    void goBack();
    void transition(AbstractCLIState nextState);
    void redirect(AbstractCLIState nextState);
    AbstractCLIState getState();
    void setState(AbstractCLIState state);
}