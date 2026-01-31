package controllers.PutPanelsControllers;
import database.HibernateUtil;
import models.Part;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.scene.control.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class PutPanelController {

    @FXML private TextField part_nrField;
    @FXML private TextField locationField;
    @FXML private ComboBox<Integer> partIDCombo; // Nasza nowa lista ID
    @FXML private Label statusLabel;
    @FXML private Button menuBut;

    /**
     * Wywoływane, gdy użytkownik wpisze Part Nr i naciśnie Enter
     * lub gdy pole straci focus.
     */
    @FXML
    public void searchPartIDs() {
        String partNr = part_nrField.getText();
        if (partNr == null || partNr.isEmpty()) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Szukamy wszystkich części o tym numerze, które nie mają jeszcze lokacji
            String hql = "SELECT p.id FROM Part p WHERE p.partNr = :nr AND (p.location IS NULL OR p.location = '')";
            Query<Integer> query = session.createQuery(hql, Integer.class);
            query.setParameter("nr", partNr);

            List<Integer> ids = query.list();
            partIDCombo.getItems().setAll(ids);

            if (ids.isEmpty()) {
                statusLabel.setText("Nie znaleziono dostępnych sztuk dla: " + partNr);
            } else {
                statusLabel.setText("Znaleziono " + ids.size() + " sztuk. Wybierz ID.");
            }
        }
    }

    @FXML
    public void putAwayPart(ActionEvent event) {
        Integer selectedID = partIDCombo.getValue();
        String location = locationField.getText();

        if (selectedID == null || location.isEmpty()) {
            statusLabel.setText("Błąd: Wybierz ID i podaj lokację!");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Part part = session.get(Part.class, selectedID);
            if (part != null) {
                part.setLocation(location);
                part.setStatus("PUTTED"); // Zmiana statusu na odłożony
                session.update(part);
                tx.commit();

                statusLabel.setText("Sukces! Przedmiot " + selectedID + " odłożony na " + location);
                statusLabel.setStyle("-fx-text-fill: green;");

                // Odświeżamy listę po odłożeniu
                searchPartIDs();
                locationField.clear();
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            statusLabel.setText("Błąd bazy danych!");
        }
    }

    @FXML
    public void menubutAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/PutMasterPanels/PutMenuPanel.fxml"));
            Stage stage = (Stage) menuBut.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
