package database;

import models.Part;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

//public class PartDAO {
//    private final SessionFactory sessionFactory;
//
//    public PartDAO(SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//
//    /**
//     * Dodaje nową część do bazy
//     */
//    public void addPart(int id, String part_nr, String part_name, long quantity, String status) {
//        try (Session session = sessionFactory.openSession()) {
//            Transaction transaction = session.beginTransaction();
//
//            Part part = new Part(id, part_nr, part_name, quantity, status);
//            // Jeśli konstruktor ustawia pola, settery poniżej są opcjonalne,
//            // chyba że konstruktor ich nie obsługuje:
//            part.setPart_id(id);
//            part.setPart_nr(part_nr);
//            part.setPart_name(part_name);
//            part.setQuantity(quantity);
//            part.setStatus(status);
//
//            session.save(part);
//            transaction.commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Kluczowa metoda dla Twojego panelu:
//     * Szuka części po part_nr i aktualizuje jej lokację oraz status.
//     */
//    public boolean updateLocationAndStatus(String partNr, String location) {
//        Transaction transaction = null;
//        try (Session session = sessionFactory.openSession()) {
//            transaction = session.beginTransaction();
//
//            // Szukamy części za pomocą HQL
//            Query<Part> query = session.createQuery("FROM Part WHERE part_nr = :nr", Part.class);
//            query.setParameter("nr", partNr);
//            Part part = query.uniqueResult();
//
//            if (part != null) {
//                part.setLocation(location);
//                part.setStatus("putted");
//
//                session.update(part);
//                transaction.commit();
//                return true; // Sukces
//            }
//            return false; // Nie znaleziono części
//        } catch (Exception e) {
//            if (transaction != null) transaction.rollback();
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * Pobiera wszystkie części z bazy danych
//     */
//    public List<Part> getAllParts() {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery("FROM Part", Part.class).list();
//        }
//    }
//
//    /**
//     * Usuwa część po jej ID
//     */
//    public void deletePart(int id) {
//        Transaction transaction = null;
//        try (Session session = sessionFactory.openSession()) {
//            transaction = session.beginTransaction();
//            Part part = session.get(Part.class, id);
//            if (part != null) {
//                session.delete(part);
//            }
//            transaction.commit();
//        } catch (Exception e) {
//            if (transaction != null) transaction.rollback();
//            e.printStackTrace();
//        }
//    }
//}

public class PartDAO {
    private SessionFactory sessionFactory;

    public PartDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // GŁÓWNA METODA: Odłożenie na lokację
    public boolean putAwayPart(String partNr, String location, int userId) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            // Szukamy części po part_nr
            Part part = session.createQuery("FROM Part WHERE part_nr = :p", Part.class)
                    .setParameter("p", partNr)
                    .uniqueResult();

            if (part != null) {
                // Aktualizujemy dane
                part.setStatus("putted");
                part.setBy_user_id(userId);
                // part.setLocation(location); // Upewnij się, że masz to pole w modelu!

                session.update(part);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public List<Part> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Part", Part.class).list();
        }
    }
}