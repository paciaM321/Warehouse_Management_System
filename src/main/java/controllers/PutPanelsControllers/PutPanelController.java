package controllers.PutPanelsControllers;

import database.HibernateUtil;
import models.Part;
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
import java.util.List;
import java.util.stream.Collectors;

public class PutPanelController {

    @FXML private TextField part_nrField, locationField;
    @FXML private ComboBox<Integer> partIDCombo;
    @FXML private Label statusLabel;
    @FXML private Button menuBut;

    @FXML private TableView<Part> partsTable;
    @FXML private TableColumn<Part, Integer> IdCol;
    @FXML private TableColumn<Part, String> partNameCol, partNrCol, locPartCol, statusCol;
    @FXML private TableColumn<Part, Long> quantityCol;

    @FXML
    public void initialize() {

        IdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        partNrCol.setCellValueFactory(new PropertyValueFactory<>("partNr"));
        locPartCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));


        loadAllProcessingParts();

        // Listener: kliknięcie w tabeli automatycznie ustawia ID w ComboBoxie i wypełnia numer produktu
        partsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                part_nrField.setText(newVal.getPartNr());
                partIDCombo.setValue(newVal.getId());
            }
        });
    }

    private void loadAllProcessingParts() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Tabela pokazuje co czeka na odłożenie nowe i zwroty
            String hql = "FROM Part WHERE status = models.PartStatus.PROCESSING OR status = models.PartStatus.RETURNED";
            Query<Part> query = session.createQuery(hql, Part.class);

            List<Part> results = query.list();
            partsTable.setItems(FXCollections.observableArrayList(results));

            if (results.isEmpty()) {
                statusLabel.setText("Poczekalnia jest pusta.");
                statusLabel.setStyle("-fx-text-fill: black;");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void searchPartIDs() {
        String partNr = part_nrField.getText().trim();
        if (partNr.isEmpty()) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Pobieramy ID tylko dla tych produktów z poczekalni, które mają ten numer
            String hql = "SELECT p.id FROM Part p WHERE p.partNr = :nr AND (p.status = models.PartStatus.PROCESSING OR p.status = models.PartStatus.RETURNED)";
            Query<Integer> query = session.createQuery(hql, Integer.class);
            query.setParameter("nr", partNr);

            List<Integer> ids = query.list();
            partIDCombo.getItems().setAll(ids);

            if (ids.isEmpty()) {
                statusLabel.setText("Brak statusu PROCESSING dla numeru: " + partNr);
            }
        }
    }

    @FXML
    public void putAwayPart(ActionEvent event) {
        Integer selectedID = partIDCombo.getValue();
        String location = locationField.getText().trim();

        if (selectedID == null || location.isEmpty()) {
            statusLabel.setText("Błąd: Wybierz ID i podaj lokację!");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            //  RĘCZNE SPRAWDZENIE PRZED TRANSAKCJĄ (Szybka walidacja)
            String hqlCheck = "SELECT count(p.id) FROM Part p WHERE p.location = :loc";
            Long count = (Long) session.createQuery(hqlCheck)
                    .setParameter("loc", location)
                    .uniqueResult();

            if (count > 0) {
                statusLabel.setText("Błąd: Lokalizacja " + location + " jest już zajęta!");
                statusLabel.setStyle("-fx-text-fill: red;");
                session.close();
                return;
            }

            //  JEŚLI WOLNA - ROZPOCZYNAMY ZAPIS
            tx = session.beginTransaction();
            Part part = session.get(Part.class, selectedID);

            if (part != null) {
                part.setLocation(location);
                part.setStatus(models.PartStatus.PUTTED);
                session.update(part);
                tx.commit();

                loadAllProcessingParts();
                statusLabel.setText("Sukces! Produkt ID:" + selectedID + " odłożony na " + location);
                statusLabel.setStyle("-fx-text-fill: green;");

                // PEŁNE CZYSZCZENIE
                locationField.clear();
                part_nrField.clear();
                partIDCombo.setValue(null);
                partIDCombo.getItems().clear();
            }
        } catch (Exception e) {
            // OBSŁUGA BŁĘDU DUPLIKATU
            if (tx != null) tx.rollback();

            if (e.getMessage().contains("ConstraintViolationException") || e.getMessage().contains("Duplicate entry")) {
                statusLabel.setText("Błąd: Lokalizacja " + location + " została właśnie zajęta!");
            } else {
                statusLabel.setText("Błąd bazy danych!");
                e.printStackTrace();
            }
            statusLabel.setStyle("-fx-text-fill: red;");
        } finally {
            session.close();
        }
    }


    @FXML
    public void menubutAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/PutMasterPanels/PutMenuPanel.fxml"));
            Stage stage = (Stage) menuBut.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
