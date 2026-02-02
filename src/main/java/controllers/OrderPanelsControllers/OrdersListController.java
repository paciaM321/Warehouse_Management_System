package controllers.OrderPanelsControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Order;
import models.OrderList;
import models.Part;

import database.HibernateUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.Button;




public class OrdersListController {
    @FXML
    private Button menuBut;

    @FXML
    private Button saveBut;

    public void saveAction(){
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            startPickingProcess(selected);
            refreshTable(); // Używamy nowej metody bezpiecznego odświeżania
        } else {
            showAlert("Błąd", "Najpierw wybierz zamówienie z tabeli!");
        }
    }

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> colID;
    @FXML private TableColumn<Order, String> colClient, colStatus, colDate;

  //  Przechowwywanie zalogowaniego uzytkownika
    public static int loggedInUserId = 1;

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("client"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        loadUserOrders();

    }

    private void loadUserOrders() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Order> query = session.createQuery("FROM Order WHERE userId = :uId AND status = 'INPROGRESS'", Order.class);
            query.setParameter("uId", loggedInUserId);
            ordersTable.setItems(FXCollections.observableArrayList(query.list()));
        }
    }

    private void startPickingProcess(Order order) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Pobieramy listę przedmiotów (OrderList) dla tego zamowienia
            Query<OrderList> query = session.createQuery("FROM OrderList WHERE orderId = :oId", OrderList.class);
            query.setParameter("oId", order.getId());
            List<OrderList> items = query.list();

            for (OrderList item : items) {
                // Dla każdego przedmiotu pobieramy dane z tabeli Part (lokalizacja, nazwa)
                Part part = session.get(Part.class, item.getPartId());
                if (part == null) continue;

                boolean pickingDone = showPickingAlert(part, item.getQuantity());

                if (pickingDone) {
                    executeDatabaseUpdate(item, part);
                } else {
                    // Jeśli pracownik kliknie Anuluj, przerywamy proces dla reszty listy
                    break;
                }
            }

            // Po zakończeniu wszystkich alertów zmieniamy status zamówienia
            updateOrderStatus(order.getId(), "PACKED");
            loadUserOrders(); // Odśwież listę
        }
    }

    private boolean showPickingAlert(Part part, int qtyToPick) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Zbiórka towaru");
        alert.setHeaderText("Idź do lokalizacji: " + part.getLocation());
        alert.setContentText("Przedmiot: " + part.getName() + " [" + part.getPartNr() + "]\n" +
                "Ilość do pobrania: " + qtyToPick);

        ButtonType buttonSubmit = new ButtonType("Zatwierdź (Submit)");
        ButtonType buttonCancel = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonSubmit, buttonCancel);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonSubmit;
    }

    private void executeDatabaseUpdate(OrderList item, Part part) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Aktualizacja postępu w zamówieniu (tabela order_items)
            item.setSubmit(item.getQuantity());
            session.update(item);

            // Logika aktualizacji stanu magazynowego (tabela parts)
            long newStockQty = part.getQuantity() - item.getQuantity();

            if (newStockQty <= 0) {
                // Zgodnie z prośbą: zamiast usuwania, czyścimy dane
                part.setQuantity(0);             // Zerujemy ilość
                part.setStatus("OUT_OF_STOCK");  // Zmiana statusu na brak towaru
                part.setLocation(null);          // Zerujemy lokalizację (null pozwoli uniknąć błędów przy UNIQUE)
            } else {
                part.setQuantity(newStockQty);
            }

            // Używamy update, co zachowuje integralność klucza obcego
            session.update(part);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    private void updateOrderStatus(int orderId, String newStatus) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Order order = session.get(Order.class, orderId);
            if (order != null) {
                order.setStatus(newStatus);
                session.update(order);
            }
            tx.commit();
        }
    }
    private void refreshTable() {
        javafx.application.Platform.runLater(() -> {
            // Czyścimy zaznaczenie, aby JavaFX nie szukał starego indeksu
            ordersTable.getSelectionModel().clearSelection();
            loadUserOrders();
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
