package controllers.PutPanelsControllers;

import controllers.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Part;
import database.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.IOException;
import java.util.List;

public class EditPartController {
    @FXML private Button menuBut, adminMenuBut;
    @FXML private TextField partNrField;
    @FXML private TextField idField, NewPartNrField, newPartNameField, newQuantityFieldq, locationfFeld;

    @FXML private TableView<Part> partsTable;
    @FXML private TableColumn<Part, Integer> colID;
    @FXML private TableColumn<Part, String> colPartNr, colPartName, colLocation, colStatus;
    @FXML private TableColumn<Part, Long> colQuantity;

    private ObservableList<Part> partListData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPartNr.setCellValueFactory(new PropertyValueFactory<>("partNr"));
        colPartName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Widoczność przycisków na podstawie roli
        if (models.UserRole.ADMIN == LoginController.loggedUser.getRole()) {
            adminMenuBut.setVisible(true);
            menuBut.setVisible(false);
        } else {
            adminMenuBut.setVisible(false);
            menuBut.setVisible(true);
        }

        partNrField.textProperty().addListener((observable, oldValue, newValue) -> searchParts(newValue));
        partsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) fillEditFields(newSel);
        });
    }

    @FXML
    public void SaveDataAction() {
        Part selectedPart = partsTable.getSelectionModel().getSelectedItem();
        if (selectedPart == null) {
            showAlert("Błąd", "Wybierz produkt z tabeli!");
            return;
        }

        //  Walidacja ilości (nieujemna, dopuszczalne 0)
        long newQty;
        try {
            newQty = Long.parseLong(newQuantityFieldq.getText());
            if (newQty < 0) {
                showAlert("Błąd walidacji", "Ilość nie może być ujemna!");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Błąd walidacji", "Ilość musi być liczbą!");
            return;
        }

        //  Logika zmiany statusu na podstawie ilości i stanu
        models.PartStatus currentStatus = selectedPart.getStatus();
        String currentLocation = selectedPart.getLocation();

        if (newQty == 0) {
            selectedPart.setStatus(models.PartStatus.OUT_OF_STOCK);
        } else if (currentStatus == models.PartStatus.OUT_OF_STOCK &&
                (currentLocation == null || currentLocation.isEmpty()) &&
                newQty > 0) {
            selectedPart.setStatus(models.PartStatus.RETURNED);
        }

        String inputLocation = locationfFeld.getText().trim();
        String newLoc = inputLocation.isEmpty() ? null : inputLocation;

        // Sprawdzenie czy coś się zmieniło (w tym status)
        boolean hasChanged = !selectedPart.getPartNr().equals(NewPartNrField.getText()) ||
                !selectedPart.getName().equals(newPartNameField.getText()) ||
                selectedPart.getQuantity() != newQty ||
                !java.util.Objects.equals(selectedPart.getLocation(), newLoc);

        if (hasChanged || selectedPart.getStatus() != currentStatus) {
            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();

                selectedPart.setPartNr(NewPartNrField.getText());
                selectedPart.setName(newPartNameField.getText());
                selectedPart.setQuantity(newQty);
                selectedPart.setLocation(newLoc);

                session.update(selectedPart);
                tx.commit();
                partsTable.refresh();
                showAlert("Sukces", "Dane zaktualizowane. Status: " + selectedPart.getStatus());
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
                showAlert("Błąd", "Nie udało się zapisać zmian: " + e.getMessage());
            }
        }
    }

    private void searchParts(String value) {
        if (value.isEmpty()) {
            partListData.clear();
            return;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Part WHERE partNr LIKE :search";
            Query<Part> query = session.createQuery(hql, Part.class);
            query.setParameter("search", "%" + value + "%");
            List<Part> results = query.list();
            partListData.setAll(results);
            partsTable.setItems(partListData);
        }
    }

    private void fillEditFields(Part part) {
        idField.setText(String.valueOf(part.getId()));
        NewPartNrField.setText(part.getPartNr());
        newPartNameField.setText(part.getName());
        newQuantityFieldq.setText(String.valueOf(part.getQuantity()));
        locationfFeld.setText(part.getLocation() != null ? part.getLocation() : "");
    }

    @FXML
    public void DelDataAction() {
        Part selectedPart = partsTable.getSelectionModel().getSelectedItem();
        if (selectedPart == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Usunąć rekord ID: " + selectedPart.getId() + "?", ButtonType.OK, ButtonType.CANCEL);
        if (confirm.showAndWait().get() == ButtonType.OK) {
            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();
                // merge() przywiązuje obiekt z oderwane sesji do nowej sesji
                Part managed = (Part) session.merge(selectedPart);
                session.delete(managed);
                tx.commit();
                partListData.remove(selectedPart);
                clearFields();
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
                showAlert("Błąd", "Nie udało się usunąć produktu: " + e.getMessage());
            }
        }
    }

    private void clearFields() {
        idField.clear(); NewPartNrField.clear(); newPartNameField.clear();
        newQuantityFieldq.clear(); locationfFeld.clear(); partNrField.clear();
        partsTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void AdminMenuAction() {
        if (models.UserRole.ADMIN == LoginController.loggedUser.getRole()) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/AdminPanels/AdminPanel.fxml"));
                Stage stage = (Stage) adminMenuBut.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    @FXML
    public void menuAction() throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/PutMasterPanels/PutMenuPanel.fxml"));
            Stage stage = (Stage) menuBut.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}