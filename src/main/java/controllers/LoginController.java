package controllers;

import controllers.OrderPanelsControllers.OrdersListController;
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

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBut;
    @FXML private Label messageLabel;

    public static User loggedUser;

    public User validateLogin(String login, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM User WHERE login = :login";
            Query<User> query = session.createQuery(hql, User.class);
            query.setParameter("login", login);
            User user = query.uniqueResult();
            if (user != null && org.mindrot.jbcrypt.BCrypt.checkpw(password, user.getPassword())) {
                return user;
            }
            return null;
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
        // PRZEKAZUJEMY CAŁY OBIEKT USER
        pause.setOnFinished(event -> loginNextPage(user));
        pause.play();
    }

    private void loginNextPage(User user) {
        try {
            loggedUser = user;
            String fxmlPath = "";
            String title = "";
            String role = user.getRole().name();

            switch (role) {
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
                messageLabel.setText("Błąd ładowania widoku dla roli: " + user.getRole());
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        }
    }
}