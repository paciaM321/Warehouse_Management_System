package database;

import models.Placement;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class PlacementDAO {
    private final SessionFactory sessionFactory;

    public PlacementDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Placement placement, Session session) {
        session.save(placement);
    }
}