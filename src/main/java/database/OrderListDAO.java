package database;

import models.OrderList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class OrderListDAO {
    private SessionFactory sessionFactory;

    public OrderListDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Zatwierdzenie pozycji na liście (submit)
    public void confirmOrderPosition(int id) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            OrderList item = session.get(OrderList.class, id);
            if (item != null) {
                item.setSubmit(1); // Zmiana statusu na zatwierdzony
                session.update(item);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw e;
        }
    }
}

