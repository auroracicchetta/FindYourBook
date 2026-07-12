package it.ispwproject.findyourbook.view.cli;

public class SearchByGenreCLIView {

    public void showHeader() {
        CLIRenderer.intestazione("ESPLORA PER GENERE");
    }

    public String askGenre() {
        CLIRenderer.sezione("Scegli una categoria");
        CLIRenderer.voceMenu(1, "Classici");
        CLIRenderer.voceMenu(2, "Fantasy");
        CLIRenderer.voceMenu(3, "Romance");
        CLIRenderer.voceMenu(4, "Gialli");
        CLIRenderer.voceMenu(5, "Avventura");
        CLIRenderer.voceMenu(6, "Poesia");
        CLIRenderer.voceMenu(7, "Storici");
        CLIRenderer.voceMenu(8, "Filosofici");
        CLIRenderer.voceMenuZero("Torna indietro");

        int choice = CLIRenderer.chiediScelta("Seleziona il genere", 0, 8);

        return switch (choice) {
            case 1 -> "classici";
            case 2 -> "fantasy";
            case 3 -> "romance";
            case 4 -> "gialli";
            case 5 -> "avventura";
            case 6 -> "poesia";
            case 7 -> "storici";
            case 8 -> "filosofici";
            default -> "0";
        };
    }
}