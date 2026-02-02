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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;


public class EditUserPanelController {
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> colUserID;
    @FXML private TableColumn<User, String> colName, colName1, colRole, colLogin, colPasw;
    @FXML private TableColumn<User, Timestamp> colCreatedAt;

    @FXML private TextField loginField, paswfield, nameField, LastnameField, roleField;
    @FXML private Button saveBut, adminmenuBut;

    private User selectedUser;

    @FXML
    public void initialize() {
        //  Konfiguracja kolumn
        colUserID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colName1.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colPasw.setCellValueFactory(new PropertyValueFactory<>("password"));
        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        loadUsers();

        //  Listener - uzupełnianie pól po wyborze użytkownika
        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedUser = newVal;
                fillFields(newVal);
            }
        });
    }

    private void loadUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User", User.class);
            List<User> list = query.list();
            usersTable.setItems(FXCollections.observableArrayList(list));
        }
    }

    private void fillFields(User user) {
        loginField.setText(user.getLogin());
        paswfield.setText(user.getPassword());
        nameField.setText(user.getFirstName());
        LastnameField.setText(user.getLastName());
        roleField.setText(user.getRole());
    }

    @FXML
    public void savebutAction(ActionEvent event) {
        if (selectedUser == null) {
            showAlert("Błąd", "Najpierw wybierz użytkownika z tabeli!");
            return;
        }

        String login = loginField.getText().trim();
        String password = paswfield.getText();
        String firstName = nameField.getText().trim();
        String lastName = LastnameField.getText().trim();
        String role = roleField.getText().trim().toUpperCase();

        //  Pełna walidacja
        StringBuilder errorMessages = new StringBuilder();

        if (firstName.isEmpty() || firstName.matches(".*\\d.*")) {
            errorMessages.append("- Imię nie może być puste ani zawierać cyfr.\n");
        }
        if (lastName.isEmpty() || lastName.matches(".*\\d.*")) {
            errorMessages.append("- Nazwisko nie może być puste ani zawierać cyfr.\n");
        }
        if (login.length() <= 3) {
            errorMessages.append("- Login musi być dłuższy niż 3 znaki.\n");
        }
        if (password.length() < 5 || !password.matches(".*[A-Z].*") || !password.matches(".*\\d.*")) {
            errorMessages.append("- Hasło: min. 5 znaków, 1 wielka litera, 1 cyfra.\n");
        }
        if (!role.equals("ADMIN") && !role.equals("PUT") && !role.equals("ORDER")) {
            errorMessages.append("- Rola musi być jedną z: ADMIN, PUT, ORDER.\n");
        }

        if (errorMessages.length() > 0) {
            showAlert("Błąd walidacji", errorMessages.toString());
            return;
        }

        //  Zapis zmian w bazie
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            selectedUser.setLogin(login);
            selectedUser.setPassword(password);
            selectedUser.setFirstName(firstName);
            selectedUser.setLastName(lastName);
            selectedUser.setRole(role);

            session.update(selectedUser);
            tx.commit();

            showAlert("Sukces", "Dane użytkownika zostały zaktualizowane.");
            loadUsers(); // Odświeżenie tabeli
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            showAlert("Błąd", "Nie udało się zaktualizować danych.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    @FXML
    public void adminMenubutAction (ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/AdminPanels/AdminPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("MenuPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage = (Stage) adminmenuBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
