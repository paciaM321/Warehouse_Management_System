package controllers.AdminPanelsControllers;

import database.HibernateUtil;
import models.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.sql.Timestamp;

public class AdminAddPanelController {
    @FXML private TextField loginField, paswField, nameField, lastnameField;
    @FXML private ComboBox<String> roleBox; // Nasz nowy ComboBox
    @FXML private Button saveBut, adminmenuBut;

    @FXML
    public void initialize() {
        // Ustawienie stałych opcji wyboru dla roli
        roleBox.setItems(FXCollections.observableArrayList("PUT", "ORDER", "ADMIN"));
    }

    @FXML
    public void savebutAction(ActionEvent event) {
        String login = loginField.getText().trim();
        String password = paswField.getText();
        String firstName = nameField.getText().trim();
        String lastName = lastnameField.getText().trim();
        String role = roleBox.getValue();

        // Zbieranie błędów
        StringBuilder errorMessages = new StringBuilder();

                                                            //.*\\d.*  ---- [0-9]
        // Walidacja Imienia i Nazwiska (brak cyfr)
        if (firstName.isEmpty()) {
            errorMessages.append("- Imię nie może być puste.\n");
        } else if (firstName.matches(".*\\d.*")) {
            errorMessages.append("- Imię nie może zawierać cyfr.\n");
        }

        if (lastName.isEmpty()) {
            errorMessages.append("- Nazwisko nie może być puste.\n");
        } else if (lastName.matches(".*\\d.*")) {
            errorMessages.append("- Nazwisko nie może zawierać cyfr.\n");
        }

        // Walidacja Loginu (> 3 znaki)
        if (login.length() <= 3) {
            errorMessages.append("- Login musi być dłuższy niż 3 znaki.\n");
        }

        // Walidacja Hasła (5+ znaków, 1 wielka litera, 1 cyfra)
        if (password.length() < 5) {
            errorMessages.append("- Hasło musi mieć co najmniej 5 znaków.\n");
        }
        if (!password.matches(".*[A-Z].*")) {
            errorMessages.append("- Hasło musi zawierać co najmniej jedną wielką literę.\n");
        }
        if (!password.matches(".*\\d.*")) {
            errorMessages.append("- Hasło musi zawierać co najmniej jedną cyfrę.\n");
        }

        // Walidacja Roli
        if (role == null) {
            errorMessages.append("- Musisz wybrać rolę (PUT, ORDER lub ADMIN).\n");
        }

        // Wyświetlenie błędów, jeśli wystąpiły
        if (errorMessages.length() > 0) {
            showAlert("Błąd walidacji", errorMessages.toString());
            return;
        }

        // Zapis do bazy danych
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            User newUser = new User();
            newUser.setLogin(login);
            newUser.setPassword(password);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setRole(role);
            newUser.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            session.save(newUser);
            tx.commit();

            showAlert("Sukces", "Użytkownik został utworzony.");
            clearFields();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            showAlert("Błąd", "Nie udało się dodać użytkownika. Login prawdopodobnie jest zajęty.");
        }
    }

    private void clearFields() {
        loginField.clear();
        paswField.clear();
        nameField.clear();
        lastnameField.clear();
        roleBox.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void adminMenubutAction(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/AdminPanels/AdminPanel.fxml"));
            Stage stage = (Stage) adminmenuBut.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}