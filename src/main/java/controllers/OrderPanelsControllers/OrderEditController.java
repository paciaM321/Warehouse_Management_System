package controllers.OrderPanelsControllers;

import controllers.LoginController;
import database.HibernateUtil;
import models.Order;
import models.OrderList;
import models.Part;
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
    @FXML private Button adminMenuBut;
    @FXML
    public void initialize() {
        // Mapowanie kolumn tabeli
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("client"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Zarządzanie widocznością przycisków (bez setManaged)
        if ("ADMIN".equalsIgnoreCase(LoginController.loggedUser.getRole())) {
            adminMenuBut.setVisible(true);
            menuBut.setVisible(false);
        } else {
            adminMenuBut.setVisible(false);
            menuBut.setVisible(true);
        }

        // Ładowanie zamówień i listener wyboru
        loadOrders();

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
        String orderIdStr = orderIDField.getText();
        if (orderIdStr.isEmpty()) {
            showAlert("Błąd", "Najpierw wybierz zamówienie z tabeli do edycji!");
            return;
        }

        //  Walidacja danych nagłówkowych
        StringBuilder errors = new StringBuilder();
        String client = clientField.getText().trim();
        String userIdStr = userIDField.getText().trim();
        String status = statusField.getText().trim().toUpperCase();

        if (client.isEmpty()) errors.append("- Nazwa klienta nie może być pusta.\n");
        if (userIdStr.isEmpty()) errors.append("- ID pracownika nie może być puste.\n");
        if (status.isEmpty()) errors.append("- Status zamówienia nie może być pusty.\n");

        //  Wstępna walidacja pozycji (ilości)
        TextField[] nrFields = {part1Field, part2Field, part3Field, part4Field, part5Field, part6Field, part7Field, part8Field, part9Field, part10Field};
        TextField[] qtyFields = {quantity1Field, quantity2Field, quantity3Field, quantity4Field, quantity5Field, quantity6Field, quantity7Field, quantity8Field, quantity9Field, quantity10Field};

        for (int i = 0; i < 10; i++) {
            String pNr = nrFields[i].getText().trim();
            String qStr = qtyFields[i].getText().trim();

            if (!pNr.isEmpty() && qStr.isEmpty()) {
                errors.append("- Pozycja ").append(i + 1).append(": Podaj ilość dla produktu ").append(pNr).append(".\n");
            } else if (!qStr.isEmpty()) {
                try {
                    int q = Integer.parseInt(qStr);
                    if (q < 0) errors.append("- Pozycja ").append(i + 1).append(": Ilość nie może być ujemna.\n");
                } catch (NumberFormatException e) {
                    errors.append("- Pozycja ").append(i + 1).append(": Ilość musi być liczbą całkowitą.\n");
                }
            }
        }

        if (errors.length() > 0) {
            showAlert("Błąd walidacji", errors.toString());
            return;
        }

        //  Zapis do bazy
        int orderId = Integer.parseInt(orderIdStr);
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Order orderToUpdate = session.get(Order.class, orderId);
            if (orderToUpdate != null) {
                orderToUpdate.setClient(client);
                orderToUpdate.setUserId(Integer.parseInt(userIdStr));
                orderToUpdate.setStatus(status);
                session.update(orderToUpdate);
            }

            updateOrderItems(session, orderId);

            tx.commit();
            showAlert("Sukces", "Zmiany w zamówieniu nr " + orderId + " zostały zapisane.");

            //  Pełne czyszczenie i odświeżenie
            loadOrders();
            clearAllFormFields();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            showAlert("Błąd", "Nie udało się zapisać zmian. Sprawdź czy ID pracownika istnieje.");
        }
    }

    // Nowa metoda czyszcząca absolutnie wszystko
    private void clearAllFormFields() {
        orderIDField.clear();
        clientField.clear();
        userIDField.clear();
        statusField.clear();
        clearPartFields(); // Czyści 30 pól pozycji (ID, Nr, Qty)
        ordersTable.getSelectionModel().clearSelection();
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void updateOrderItems(Session session, int orderId) {
        // Tablice pól z FXML
        TextField[] nrFields = {part1Field, part2Field, part3Field, part4Field, part5Field, part6Field, part7Field, part8Field, part9Field, part10Field};
        TextField[] qtyFields = {quantity1Field, quantity2Field, quantity3Field, quantity4Field, quantity5Field, quantity6Field, quantity7Field, quantity8Field, quantity9Field, quantity10Field};
        TextField[] idFields = {partID1Field, partID2Field, partID3Field, partID4Field, partID5Field, partID6Field, partID7Field, partID8Field, partID9Field, partID10Field};

        // Pobieramy aktualne pozycje z bazy, aby wiedzieć co edytować
        Query<OrderList> query = session.createQuery("FROM OrderList WHERE orderId = :oId", OrderList.class);
        query.setParameter("oId", orderId);
        List<OrderList> existingItems = query.list();

        for (int i = 0; i < 10; i++) {
            String partNr = nrFields[i].getText().trim();
            String qtyStr = qtyFields[i].getText().trim();

            if (!partNr.isEmpty() && !qtyStr.isEmpty()) {
                int quantity = Integer.parseInt(qtyStr);

                // Sprawdzamy, czy ta pozycja (i-ta) już istniała w bazie
                if (i < existingItems.size()) {
                    OrderList item = existingItems.get(i);
                    item.setQuantity(quantity);
                    session.update(item);
                } else {
                    // Jeśli użytkownik dopisał nową pozycję w wolnym slocie
                    // Musimy znaleźć ID części na podstawie wpisanego Part Nr
                    Query<Part> partQuery = session.createQuery("FROM Part WHERE partNr = :nr AND quantity > 0", Part.class);
                    partQuery.setParameter("nr", partNr);
                    partQuery.setMaxResults(1);
                    Part part = partQuery.uniqueResult();

                    if (part != null) {
                        OrderList newItem = new OrderList();
                        newItem.setOrderId(orderId);
                        newItem.setPartId(part.getId());
                        newItem.setQuantity(quantity);
                        newItem.setSubmit(0);
                        session.save(newItem);
                    }
                }
            }
        }
    }

    @FXML
    public void AdminMenuAction() {
        User currentUser = LoginController.loggedUser;
        if (currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/view/AdminPanels/AdminPanel.fxml"));

                Stage stage = new Stage();
                stage.setTitle("menu");
                stage.setScene(new Scene(root));
                stage.show();


                Stage mainStage = (Stage) adminMenuBut.getScene().getWindow();
                mainStage.hide();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
