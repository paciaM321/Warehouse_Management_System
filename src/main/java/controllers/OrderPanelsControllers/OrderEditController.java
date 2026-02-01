package controllers.OrderPanelsControllers;

import database.HibernateUtil;
import models.Order;
import models.OrderList;
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
import org.hibernate.query.Query;

import java.io.IOException;
import java.util.List;

public class OrderEditController {

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, String> colClient, colStatus;

    @FXML private TextField orderIDField, clientField, userIDField, statusField;

    // Grupy pól dla przedmiotów
    @FXML private TextField part1Field, part2Field, part3Field, part4Field, part5Field, part6Field, part7Field, part8Field, part9Field, part10Field;
    @FXML private TextField quantity1Field, quantity2Field, quantity3Field, quantity4Field, quantity5Field, quantity6Field, quantity7Field, quantity8Field, quantity9Field, quantity10Field;
    @FXML private TextField partID1Field, partID2Field, partID3Field, partID4Field, partID5Field, partID6Field, partID7Field, partID8Field, partID9Field, partID10Field;

    @FXML private Button menuBut, saveBut;

    @FXML
    public void initialize() {
        // 1. Mapowanie kolumn tabeli
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("client"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 2. Ładowanie zamówień (status != PACKED)
        loadOrders();

        // 3. Listener wyboru wiersza
        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillFields(newVal);
            }
        });
    }

    private void loadOrders() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Pobieramy tylko te, które nie są jeszcze spakowane
            Query<Order> query = session.createQuery("FROM Order WHERE status != 'PACKED'", Order.class);
            ordersTable.setItems(FXCollections.observableArrayList(query.list()));
        }
    }

    private void fillFields(Order order) {
        // Czyszczenie pól przed nowym wypełnieniem
        clearPartFields();

        // Podstawowe dane zamówienia
        orderIDField.setText(String.valueOf(order.getId()));
        clientField.setText(order.getClient());
        userIDField.setText(String.valueOf(order.getUserId()));
        statusField.setText(order.getStatus());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Pobieranie przedmiotów przypisanych do tego zamówienia
            Query<OrderList> query = session.createQuery("FROM OrderList WHERE orderId = :oId", OrderList.class);
            query.setParameter("oId", order.getId());
            List<OrderList> items = query.list();

            // Tablice pomocnicze do iteracji po TextFieldach
            TextField[] nrFields = {part1Field, part2Field, part3Field, part4Field, part5Field, part6Field, part7Field, part8Field, part9Field, part10Field};
            TextField[] qtyFields = {quantity1Field, quantity2Field, quantity3Field, quantity4Field, quantity5Field, quantity6Field, quantity7Field, quantity8Field, quantity9Field, quantity10Field};
            TextField[] idFields = {partID1Field, partID2Field, partID3Field, partID4Field, partID5Field, partID6Field, partID7Field, partID8Field, partID9Field, partID10Field};

            for (int i = 0; i < items.size() && i < 10; i++) {
                OrderList item = items.get(i);

                // Pobieramy numer części na podstawie ID
                Part part = session.get(Part.class, item.getPartId());

                if (part != null) {
                    nrFields[i].setText(part.getPartNr());
                    qtyFields[i].setText(String.valueOf(item.getQuantity()));
                    idFields[i].setText(String.valueOf(part.getId()));
                }
            }
        }
    }

    private void clearPartFields() {
        TextField[] fields = {part1Field, part2Field, part3Field, part4Field, part5Field, part6Field, part7Field, part8Field, part9Field, part10Field,
                quantity1Field, quantity2Field, quantity3Field, quantity4Field, quantity5Field, quantity6Field, quantity7Field, quantity8Field, quantity9Field, quantity10Field,
                partID1Field, partID2Field, partID3Field, partID4Field, partID5Field, partID6Field, partID7Field, partID8Field, partID9Field, partID10Field};
        for (TextField f : fields) f.clear();
    }

    @FXML
    public void savebutAction(ActionEvent event) {
        // Tutaj dodasz logikę zapisu zmian w przyszłości
    }

    public void menubutAction (ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/OrderMasterPanel/OrderMasterMenuPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("MenuPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage = (Stage) menuBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
