package it.ispwproject.findyourbook.controller.cli;

import it.ispwproject.findyourbook.pattern.state.CLIStateMachine;
import it.ispwproject.findyourbook.pattern.state.CLIStateMachineImpl;

public class MainCLI {

    public static void start() {
        CLIStateMachine machine = new CLIStateMachineImpl();
        machine.start();
    }
}