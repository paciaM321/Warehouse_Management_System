package controllers.OrderPanelsControllers;

import controllers.LoginController;
import database.HibernateUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Order;
import models.OrderList;
import models.Part;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class OrdersListController {
    @FXML private Button menuBut, saveBut;
    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> colID;
    @FXML private TableColumn<Order, String> colClient, colStatus, colDate;

    // Pobieramy ID zalogowanego użytkownika
    private int loggedInUserId = LoginController.loggedUser.getId();

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("client"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        loadUserOrders();
    }

    @FXML
    public void saveAction() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            startPickingProcess(selected);
        } else {
            showAlert("Błąd", "Najpierw wybierz zamówienie z tabeli!");
        }
    }

    private void loadUserOrders() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Pokazujemy zamówienia przypisane do pracownika, które są w toku
            Query<Order> query = session.createQuery("FROM Order WHERE user.id = :uId AND status = models.OrderStatus.INPROGRESS", Order.class);
            query.setParameter("uId", loggedInUserId);
            ordersTable.setItems(FXCollections.observableArrayList(query.list()));
        }
    }

    private void startPickingProcess(Order order) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Pobieramy tylko te pozycje, które nie zostały jeszcze zebrane (submit < quantity)
            Query<OrderList> query = session.createQuery("FROM OrderList WHERE order.id = :oId AND submit < quantity", OrderList.class);
            query.setParameter("oId", order.getId());
            List<OrderList> itemsToPick = query.list();

            if (itemsToPick.isEmpty()) {
                showAlert("Info", "Wszystkie produkty z tego zamówienia są już zebrane.");
                checkAndFinalizeOrder(order.getId());
                return;
            }

            boolean processInterrupted = false;

            for (OrderList item : itemsToPick) {
                Part part = session.get(Part.class, item.getPartId());
                if (part == null) continue;

                // Alert prowadzący magazyniera po lokalizacjach
                boolean picked = showPickingAlert(part, item.getQuantity());

                if (picked) {
                    confirmItemPick(item.getId()); // Potwierdzamy zbiórkę w bazie
                } else {
                    // Jeśli kliknie ANULUJ - przerywamy pętlę (zawieszamy)
                    processInterrupted = true;
                    break;
                }
            }

            if (processInterrupted) {
                // Specjalny komunikat przy przerwaniu
                showAlert("ZAMÓWIENIE ZAWIESZONE", "Proces zbierania przerwany. Skontaktuj się z przełożonym!");
            } else {
                // 4. Jeśli przeszliśmy przez wszystko bez przerw - sprawdzamy finał
                checkAndFinalizeOrder(order.getId());
            }

            refreshTable();
        }
    }

    private void confirmItemPick(int orderListId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            OrderList item = session.get(OrderList.class, orderListId);
            if (item != null) {
                // Ustawiamy submit na quantity - to oznacza "fizycznie pobrane z półki"
                item.setSubmit(item.getQuantity());
                session.update(item);

                // Zwalniamy lokalizację jeśli część jest OUT_OF_STOCK
                Part part = item.getPart();
                if (part != null && part.getQuantity() == 0) {
                    part.setLocation(null);
                    session.update(part);
                }
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    private void checkAndFinalizeOrder(int orderId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Sprawdzamy czy w tym zamówieniu zostało COKOLWIEK do zebrania
            Query<Long> query = session.createQuery("SELECT count(id) FROM OrderList WHERE order.id = :oId AND submit < quantity", Long.class);
            query.setParameter("oId", orderId);

            if (query.uniqueResult() == 0) {
                updateOrderStatus(orderId, models.OrderStatus.PACKED);
                showAlert("UKOŃCZONO", "Zamówienie skompletowane! Proszę złożyć produkty do wysyłki.");
            }
        }
    }

    private void updateOrderStatus(int orderId, models.OrderStatus newStatus) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Order order = session.get(Order.class, orderId);
            if (order != null) {
                order.setStatus(newStatus);
                session.update(order);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    private boolean showPickingAlert(Part part, int qtyToPick) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Zbiórka towaru");
        alert.setHeaderText("IDŹ DO: " + (part.getLocation() != null ? part.getLocation() : "BRAK LOKALIZACJI"));
        alert.setContentText("Przedmiot: " + part.getName() + "\nNr: " + part.getPartNr() + "\nIlość: " + qtyToPick);

        ButtonType buttonSubmit = new ButtonType("Zatwierdź (Submit)");
        ButtonType buttonCancel = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonSubmit, buttonCancel);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonSubmit;
    }

    private void refreshTable() {
        Platform.runLater(() -> {
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

    @FXML
    public void menubutAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/OrderMasterPanel/OrderMasterMenuPanel.fxml"));
            Stage stage = (Stage) menuBut.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
}