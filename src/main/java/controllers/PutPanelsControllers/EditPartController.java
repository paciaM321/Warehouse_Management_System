package controllers.PutPanelsControllers;
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
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.IOException;
import java.util.List;

public class EditPartController {
@FXML
private Button menuBut;

    @FXML
    private TextField partNrField; // pole wyszukiwania
    @FXML
    private TextField idField, NewPartNrField, newPartNameField, newQuantityFieldq, locationfFeld;
    @FXML
    private TableView<Part> partsTable;
    @FXML
    private TableColumn<Part, Integer> colID;
    @FXML
    private TableColumn<Part, String> colPartNr, colPartName, colLocation;
    @FXML
    private TableColumn<Part, Long> colQuantity;

    private ObservableList<Part> partListData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Konfiguracja kolumn (muszą pasować do nazw pól w klasie Part)
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPartNr.setCellValueFactory(new PropertyValueFactory<>("partNr"));
        colPartName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));

        // 2. Obsługa wyszukiwania podczas wpisywania (lub podepnij pod przycisk)
        partNrField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchParts(newValue);
        });

        // 3. Listener wyboru w tabeli - wypełnianie pól edycji
        partsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillEditFields(newSelection);
            }
        });
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
        locationfFeld.setText(part.getLocation());
    }

    @FXML
    public void SaveDataAction() {
        Part selectedPart = partsTable.getSelectionModel().getSelectedItem();
        if (selectedPart == null) return;

        // WALIDACJA QUANTITY (tylko liczby)
        long newQty;
        try {
            newQty = Long.parseLong(newQuantityFieldq.getText());
        } catch (NumberFormatException e) {
            showAlert("Błąd walidacji", "Pole Quantity musi być liczbą!");
            return;
        }

        // SPRAWDZENIE CZY COŚ SIĘ ZMIENIŁO
        boolean hasChanged = !selectedPart.getPartNr().equals(NewPartNrField.getText()) ||
                !selectedPart.getName().equals(newPartNameField.getText()) ||
                selectedPart.getQuantity() != newQty ||
                !selectedPart.getLocation().equals(locationfFeld.getText());

        if (hasChanged) {
            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();

                selectedPart.setPartNr(NewPartNrField.getText());
                selectedPart.setName(newPartNameField.getText());
                selectedPart.setQuantity(newQty);
                selectedPart.setLocation(locationfFeld.getText());

                session.update(selectedPart);
                tx.commit();
                partsTable.refresh();
                showAlert("Sukces", "Dane produktu zostały zaktualizowane.");
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            }
        } else {
            System.out.println("Brak zmian - nie aktualizuję bazy.");
        }
    }

    @FXML
    public void DelDataAction() {
        // 1. Pobieramy zaznaczony produkt z tabeli
        Part selectedPart = partsTable.getSelectionModel().getSelectedItem();

        if (selectedPart == null) {
            showAlert("Błąd", "Najpierw zaznacz produkt w tabeli, który chcesz usunąć!");
            return;
        }

        // 2. Okno potwierdzenia (Security check)
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Potwierdzenie usunięcia");
        confirm.setHeaderText("Czy na pewno chcesz usunąć produkt?");
        confirm.setContentText("ID: " + selectedPart.getId() + "\nNazwa: " + selectedPart.getName());

        if (confirm.showAndWait().get() == ButtonType.OK) {
            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();

                // Hibernate usuwa obiekt na podstawie jego ID
                session.delete(selectedPart);

                tx.commit();

                // 3. Odświeżamy UI
                partListData.remove(selectedPart);
                clearFields(); // Metoda czyszcząca pola tekstowe
                showAlert("Sukces", "Produkt został trwale usunięty z bazy.");

            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
                showAlert("Błąd", "Nie udało się usunąć produktu z bazy danych.");
            }
        }
    }

    private void clearFields() {
        // Czyszczenie wszystkich pól tekstowych na dole panelu
        idField.clear();
        NewPartNrField.clear();
        newPartNameField.clear();
        newQuantityFieldq.clear();
        locationfFeld.clear();

        // Opcjonalnie: czyścimy pole wyszukiwania, jeśli chcemy zacząć od nowa
        partNrField.clear();

        // Ważne: usuwamy zaznaczenie z tabeli, aby uniknąć pomyłek
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
    public void menuAction() throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/PutMasterPanels/PutMenuPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("menu");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) menuBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}