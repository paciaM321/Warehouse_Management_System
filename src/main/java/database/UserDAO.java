package database;

import models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class UserDAO {
    private SessionFactory sessionFactory;

    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Pobranie użytkownika po loginie (do logowania)
    public User findByLogin(String login) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE login = :l", User.class)
                    .setParameter("l", login)
                    .uniqueResult();
        }
    }
}