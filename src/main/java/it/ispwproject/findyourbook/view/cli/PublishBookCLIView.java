package it.ispwproject.findyourbook.view.cli;

public class PublishBookCLIView {

    public void showHeader() {
        CLIRenderer.messaggio("\n=== PUBBLICA UN NUOVO LIBRO ===");
        CLIRenderer.messaggio("(Digita '0' in qualsiasi momento per annullare e tornare indietro)");
    }

    public String askField(String prompt) {
        showGlobalPrompt(prompt + ": ");
        return CLIRenderer.SCANNER.nextLine().trim();
    }

    public void showMessage(String msg) {
        CLIRenderer.messaggio("-> " + msg);
    }

    private void showGlobalPrompt(String prompt) {
        CLIRenderer.messaggio(prompt);
    }
}