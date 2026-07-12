package it.ispwproject.findyourbook.dao;

import it.ispwproject.findyourbook.dao.db.LoginDAODB;
import it.ispwproject.findyourbook.dao.db.PublisherDAODB;
import it.ispwproject.findyourbook.dao.db.ReaderDAODB;
import it.ispwproject.findyourbook.dao.db.RegistrationDAODB;
import it.ispwproject.findyourbook.dao.db.UserDAODB;
import it.ispwproject.findyourbook.dao.file.PublisherDAOFile;
import it.ispwproject.findyourbook.dao.memory.LoginDAOMemory;
import it.ispwproject.findyourbook.dao.memory.PublisherDAOMemory;
import it.ispwproject.findyourbook.dao.memory.ReaderDAOMemory;
import it.ispwproject.findyourbook.dao.memory.RegistrationDAOMemory;
import it.ispwproject.findyourbook.dao.memory.UserDAOMemory;
import it.ispwproject.findyourbook.dao.memory.BookDAOMemory;
import it.ispwproject.findyourbook.dao.db.BookDAODB;
import it.ispwproject.findyourbook.dao.file.BookDAOFile;
import it.ispwproject.findyourbook.dao.file.ReaderDAOFile;


public class DAOFactory {

    public static final String DATABASE = "database";
    public static final String MEMORY   = "memory";
    public static final String FILE     = "file";

    private static String persistence = DATABASE;

    private DAOFactory() {}

    public static void setPersistence(String mode) {
        if (mode != null && !mode.isBlank()) {
            persistence = mode;
        }
    }

    public static String getPersistence() {
        return persistence;
    }

    public static LoginDAO getLoginDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new LoginDAOMemory();
        return new LoginDAODB();
    }

    public static UserDAO getUserDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new UserDAOMemory();
        return new UserDAODB();
    }

    public static RegistrationDAO getRegistrationDAO() {
        if (MEMORY.equalsIgnoreCase(persistence)) return new RegistrationDAOMemory();
        return new RegistrationDAODB();
    }

    public static ReaderDAO getReaderDAO() {
        return switch (persistence.toLowerCase()) {
            case FILE   -> new ReaderDAOFile();
            case MEMORY -> new ReaderDAOMemory();
            default     -> new ReaderDAODB();
        };
    }

    public static PublisherDAO getPublisherDAO() {
        return switch (persistence.toLowerCase()) {
            case FILE   -> new PublisherDAOFile();
            case MEMORY -> new PublisherDAOMemory();
            default     -> new PublisherDAODB();
        };
    }

    public static BookDAO getBookDAO() {
        return switch (persistence.toLowerCase()) {
            case FILE   -> new BookDAOFile();
            case MEMORY -> new BookDAOMemory();
            default     -> new BookDAODB();
        };
    }

}