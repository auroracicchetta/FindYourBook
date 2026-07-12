package it.ispwproject.findyourbook.view.cli;

import it.ispwproject.findyourbook.bean.BookBean;
import java.util.Scanner;

public class PublishBookCLIView {
    private final Scanner scanner = new Scanner(System.in);

    public void showHeader() {
        System.out.println("\n=== PUBBLICA UN NUOVO LIBRO ===");
        System.out.println("(Digita '0' in qualsiasi momento per annullare e tornare indietro)");
    }

    public String askField(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    public void showMessage(String msg) {
        System.out.println("-> " + msg);
    }
}