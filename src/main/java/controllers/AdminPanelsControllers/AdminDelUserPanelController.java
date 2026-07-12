package controllers.AdminPanelsControllers;

import database.HibernateUtil;
import models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class AdminDelUserPanelController {

    @FXML private TableView<User> UsersTable;
    @FXML private TableColumn<User, Integer> colUserID;
    @FXML private TableColumn<User, String> colName, colLastname, colRole;
    @FXML private TableColumn<User, Timestamp> colCreatedAt;

    @FXML private TextField User_id;
    @FXML private Button deleteBut, adminmenuBut;

    @FXML
    public void initialize() {
        //  Mapowanie kolumn tabeli do pól modelu User
        colUserID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colCreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        //   Załadowanie danych do tabeli
        loadUsersData();

        //   Opcjonalnie: Uzupełnianie pola tekstowego po kliknięciu w wiersz
        UsersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                User_id.setText(String.valueOf(newVal.getId()));
            }
        });
    }

    private void loadUsersData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User", User.class);
            List<User> userList = query.list();
            ObservableList<User> users = FXCollections.observableArrayList(userList);
            UsersTable.setItems(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void deletebutAction(ActionEvent event) {
        String idToDelete = User_id.getText();

        if (idToDelete.isEmpty()) {
            showAlert("Błąd", "Wprowadź ID użytkownika do usunięcia.");
            return;
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            int id = Integer.parseInt(idToDelete);
            User user = session.get(User.class, id);

            if (user != null) {
                // Sprawdzenie roli - bezpieczeństwo
                if (models.UserRole.ADMIN == user.getRole()) {
                    showAlert("Ostrzeżenie", "Usuwanie administratorów wymaga dodatkowych uprawnień.");
                    tx.rollback();
                    return;
                }

                session.delete(user);
                tx.commit();

                showAlert("Sukces", "Użytkownik o ID " + id + " został usunięty.");
                User_id.clear();
                loadUsersData(); // Odświeżenie tabeli
            } else {
                showAlert("Błąd", " Nie znaleziono użytkownika o podanym ID.");
            }

        } catch (org.hibernate.exception.ConstraintViolationException e) {
            if (tx != null) tx.rollback();
            showAlert("Błąd Integralności", "Nie można usunąć użytkownika, ponieważ jest przypisany do zamówień.");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            showAlert("Błąd", "Wystąpił nieoczekiwany błąd podczas usuwania.");
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
    public void adminMenubutAction(ActionEvent event) {
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