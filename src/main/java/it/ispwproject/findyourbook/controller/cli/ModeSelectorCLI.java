package it.ispwproject.findyourbook.controller.cli;

import it.ispwproject.findyourbook.dao.DAOFactory;
import it.ispwproject.findyourbook.view.cli.ModeSelectorView;

public class ModeSelectorCLI {

    private final ModeSelectorView view = new ModeSelectorView();

    public boolean start() {
        while (true) {
            view.mostraMenu();
            String scelta = view.chiediScelta();

            switch (scelta) {
                case "1" -> {
                    DAOFactory.setPersistence(DAOFactory.MEMORY);
                    view.mostraModalitaSelezionata("Demo (in-memory)");
                    return true;
                }
                case "2" -> {
                    DAOFactory.setPersistence(DAOFactory.DATABASE);
                    view.mostraModalitaSelezionata("Database (MySQL)");
                    return true;
                }
                case "3" -> {
                    DAOFactory.setPersistence("file");
                    view.mostraModalitaSelezionata("File (JSON)");
                    return true;
                }
                case "0" -> {
                    return false;
                }
                default -> view.mostraErrore("Scelta non valida.");
            }
        }
    }
}