
package controllers;
import models.User;
import database.HibernateUtil;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.io.IOException;


public class LoginController {

    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginBut;
    @FXML
    private Label messageLabel;


    public User validateLogin(String login, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Zapytanie HQL (Hibernate Query Language)
            String hql = "FROM User WHERE login = :login AND password = :password";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("login", login);
            query.setParameter("password", password);

            return query.uniqueResult(); // Zwróci obiekt User lub null jeśli nie znajdzie
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    protected void login() {
        String inputLogin = loginField.getText();
        String inputHaslo = passwordField.getText();

        User loggedInUser = validateLogin(inputLogin, inputHaslo);

        if (loggedInUser != null) {
            if (messageLabel != null) messageLabel.setText("Zalogowano jako: " + loggedInUser.getRole());
            przejdzDoAplikacji(loggedInUser);
        } else {
            if (messageLabel != null) messageLabel.setText("Błędny login lub hasło!");
        }
    }


    private void przejdzDoAplikacji(User user) {
        messageLabel.setText("Zalogowano pomyślnie jako " + user.getRole() + ". Przekierowanie...");
        messageLabel.setStyle("-fx-text-fill: green;");

        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(event -> loginNextPage(user.getRole())); // Przekazujemy rolę
        pause.play();
    }

    private void loginNextPage(String role) {
        try {
            String fxmlPath = "";
            String title = "";

            // Logika wyboru odpowiedniego widoku na podstawie roli
            switch (role.toUpperCase()) {
                case "ADMIN":
                    fxmlPath = "/view/AdminPanels/AdminPanel.fxml";
                    title = "Panel Administratora";
                    break;
                case "PUT":
                    fxmlPath = "/view/PutMasterPanels/PutMenuPanel.fxml";
                    title = "Panel Przyjęć (PUT)";
                    break;
                case "ORDER":
                    fxmlPath = "/view/OrderMasterPanel/OrderMasterMenuPanel.fxml";
                    title = "Panel Zamówień (ORDER)";
                    break;
                default:
                    fxmlPath = "/view/WorkersPanels/LoginPanel.fxml";
                    title = "login";
                    break;
            }

            // Ładowanie wybranego pliku FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

            Stage mainStage = (Stage) loginBut.getScene().getWindow();
            mainStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            if (messageLabel != null) {
                messageLabel.setText("Błąd ładowania widoku: " + role);
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }

    }
