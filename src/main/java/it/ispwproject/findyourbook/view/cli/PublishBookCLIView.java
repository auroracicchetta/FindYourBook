package it.ispwproject.findyourbook.view.cli;

import java.util.Scanner;

public class PublishBookCLIView {
    private final Scanner scanner = new Scanner(System.in);

    public void showHeader() {
        showGlobalMessage("\n=== PUBBLICA UN NUOVO LIBRO ===");
        showGlobalMessage("(Digita '0' in qualsiasi momento per annullare e tornare indietro)");
    }

    public String askField(String prompt) {
        showGlobalPrompt(prompt + ": ");
        return scanner.nextLine().trim();
    }

    public void showMessage(String msg) {
        showGlobalMessage("-> " + msg);
    }

    private void showGlobalMessage(String message) {
        System.out.println(message);
    }

    private void showGlobalPrompt(String prompt) {
        showGlobalMessage(prompt);
    }
}