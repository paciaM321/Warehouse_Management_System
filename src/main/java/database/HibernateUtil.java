package database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Konfiguracja wczytuje plik hibernate.cfg.xml z folderu resources
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Błąd podczas tworzenia SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Zamknięcie połączenia przy wyłączaniu aplikacji
        getSessionFactory().close();
    }
}