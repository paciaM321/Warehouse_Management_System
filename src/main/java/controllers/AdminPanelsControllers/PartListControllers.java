package controllers.AdminPanelsControllers;

import database.HibernateUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import models.Part;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.IOException;
import java.util.List;

public class PartListControllers {

    @FXML private TableView<Part> partsTable;
    @FXML private TableColumn<Part, Integer> IdCol;
    @FXML private TableColumn<Part, String> partNameCol, partNrCol, locPartCol, statusCol;
    @FXML private TableColumn<Part, Long> quantityCol;
    @FXML private Button adminmenuBut;

    @FXML
    public void initialize() {
        //  Mapowanie kolumn do pól w modelu Part
        IdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        partNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        partNrCol.setCellValueFactory(new PropertyValueFactory<>("partNr")); // Musi pasować do pola w klasie Part
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        locPartCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        //  Pobranie danych z bazy
        loadPartsData();
    }

    private void loadPartsData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Pobieramy wszystkie produkty bez filtrowania
            Query<Part> query = session.createQuery("FROM Part", Part.class);
            List<Part> allParts = query.list();

            //  Wrzucenie listy do tabeli JavaFX
            partsTable.setItems(FXCollections.observableArrayList(allParts));
        } catch (Exception e) {
            e.printStackTrace();
        }
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