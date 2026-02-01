package controllers.OrderPanelsControllers;

import database.HibernateUtil;
import models.Order;
import models.OrderList;
import models.Part;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OrderAddController {

    @FXML private TextField clientField, userIDField;
    @FXML private Label orderIDLabel;
    @FXML private Button menuBut;

    // Listy pól tekstowych dla łatwiejszej obsługi w pętli
    @FXML private TextField part1Field, part2Field, part3Field, part4Field, part5Field, part6Field, part7Field, part8Field, part9Field, part10Field;
    @FXML private TextField quantity1Field, quantity2Field, quantity3Field, quantity4Field, quantity5Field, quantity6Field, quantity7Field, quantity8Field, quantity9Field, quantity10Field;

    @FXML
    public void savebutAction(ActionEvent event) {
        String client = clientField.getText();
        String workerIdStr = userIDField.getText();

        if (client.isEmpty() || workerIdStr.isEmpty()) {
            showAlert("Błąd", "Podaj klienta i ID pracownika!");
            return;
        }

        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // 1. Tworzenie zamówienia ze statusem "INPROGRESS"
            Order newOrder = new Order();
            newOrder.setClient(client);
            newOrder.setUserId(Integer.parseInt(workerIdStr));
            newOrder.setOrderDate(new Timestamp(System.currentTimeMillis()));
            newOrder.setStatus("INPROGRESS"); // Zmieniono z NEW na INPROGRESS

            session.save(newOrder);

            // 2. Inteligentne zapisywanie przedmiotów (z obsługą wielu lokalizacji)
            allocateItems(session, newOrder.getId());

            tx.commit();
            orderIDLabel.setText("Zamówienie nr: " + newOrder.getId());
            showAlert("Sukces", "Zamówienie INPROGRESS zapisane!");
            clearAllFields();

        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) tx.rollback();
            e.printStackTrace();
            showAlert("Błąd", "Błąd alokacji! Sprawdź czy masz dość towaru w magazynie.");
        }
    }

    private void allocateItems(Session session, int orderId) {
        TextField[] partFields = {part1Field, part2Field, part3Field, part4Field, part5Field, part6Field, part7Field, part8Field, part9Field, part10Field};
        TextField[] qtyFields = {quantity1Field, quantity2Field, quantity3Field, quantity4Field, quantity5Field, quantity6Field, quantity7Field, quantity8Field, quantity9Field, quantity10Field};

        for (int i = 0; i < partFields.length; i++) {
            String partNr = partFields[i].getText().trim();
            String qtyStr = qtyFields[i].getText().trim();

            if (!partNr.isEmpty() && !qtyStr.isEmpty()) {
                int remainingToAllocate = Integer.parseInt(qtyStr);

                // Pobieramy wszystkie partie tego produktu, które mają stan > 0, sortując po ilości (od największej)
                Query<Part> query = session.createQuery("FROM Part WHERE partNr = :nr AND quantity > 0 ORDER BY quantity DESC", Part.class);
                query.setParameter("nr", partNr);
                List<Part> availableStocks = query.list();

                for (Part stock : availableStocks) {
                    if (remainingToAllocate <= 0) break;

                    // Ile możemy wziąć z tej konkretnej lokalizacji (ID)?
                    int take = (int) Math.min(stock.getQuantity(), remainingToAllocate);

                    OrderList item = new OrderList();
                    item.setOrderId(orderId);
                    item.setPartId(stock.getId()); // Zapisujemy konkretne ID lokalizacji
                    item.setQuantity(take);
                    item.setSubmit(0);
                    session.save(item);

                    remainingToAllocate -= take;
                }

                if (remainingToAllocate > 0) {
                    throw new RuntimeException("Brak wystarczającej ilości towaru: " + partNr);
                }
            }
        }
    }

    private void clearAllFields() {
        clientField.clear(); userIDField.clear();
        // Można dodać pętlę czyszczącą wszystkie 20 TextFieldów
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void menubutAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/OrderMasterPanel/OrderMasterMenuPanel.fxml"));
            Stage stage = (Stage) menuBut.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }
}