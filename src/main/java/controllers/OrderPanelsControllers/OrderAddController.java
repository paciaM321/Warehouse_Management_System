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
import java.util.List;

public class OrderAddController {

    @FXML private TextField clientField, userIDField;
    @FXML private Label orderIDLabel;
    @FXML private Button menuBut;

    @FXML private TextField part1Field, part2Field, part3Field, part4Field, part5Field, part6Field, part7Field, part8Field, part9Field, part10Field;
    @FXML private TextField quantity1Field, quantity2Field, quantity3Field, quantity4Field, quantity5Field, quantity6Field, quantity7Field, quantity8Field, quantity9Field, quantity10Field;

    @FXML
    public void savebutAction(ActionEvent event) {
        StringBuilder errors = new StringBuilder();
        String client = clientField.getText().trim();
        String workerIdStr = userIDField.getText().trim();

        //  Walidacja nagłówka
        if (client.isEmpty()) errors.append("- Nazwa firmy nie może być pusta.\n");
        if (workerIdStr.isEmpty()) errors.append("- ID pracownika nie może być puste.\n");

        //  Walidacja produktów
        TextField[] partFields = {part1Field, part2Field, part3Field, part4Field, part5Field, part6Field, part7Field, part8Field, part9Field, part10Field};
        TextField[] qtyFields = {quantity1Field, quantity2Field, quantity3Field, quantity4Field, quantity5Field, quantity6Field, quantity7Field, quantity8Field, quantity9Field, quantity10Field};

        boolean hasAtLeastOneItem = false;
        for (int i = 0; i < partFields.length; i++) {
            String partNr = partFields[i].getText().trim();
            String qtyStr = qtyFields[i].getText().trim();

            if (!partNr.isEmpty() || !qtyStr.isEmpty()) {
                if (partNr.isEmpty() || qtyStr.isEmpty()) {
                    errors.append("- Pozycja ").append(i + 1).append(": Podaj numer części i ilość.\n");
                } else {
                    try {
                        int qty = Integer.parseInt(qtyStr);
                        if (qty <= 0) errors.append("- Pozycja ").append(i + 1).append(": Ilość musi być > 0.\n");
                        else hasAtLeastOneItem = true;
                    } catch (NumberFormatException e) {
                        errors.append("- Pozycja ").append(i + 1).append(": Ilość musi być liczbą.\n");
                    }
                }
            }
        }

        if (!hasAtLeastOneItem) errors.append("- Dodaj co najmniej jeden produkt do zamówienia.\n");

        if (errors.length() > 0) {
            showAlert("Błąd walidacji", errors.toString());
            return;
        }

        //  Bezpieczny zapis Hibernate (bez try-with-resources dla sesji)
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            int workerId = Integer.parseInt(workerIdStr);
            models.User worker = session.get(models.User.class, workerId);
            if (worker == null) {
                throw new RuntimeException("Nie znaleziono pracownika o ID: " + workerId);
            }

            Order newOrder = new Order();
            newOrder.setClient(client);
            newOrder.setUser(worker);
            newOrder.setOrderDate(new Timestamp(System.currentTimeMillis()));
            newOrder.setStatus(models.OrderStatus.INPROGRESS);

            session.save(newOrder);
            allocateItems(session, newOrder);

            tx.commit();
            orderIDLabel.setText("Zamówienie nr: " + newOrder.getId());
            showAlert("Sukces", "Zamówienie zapisane!");
            clearAllFields();

        } catch (Exception e) {
            // TERAZ ROLLBACK ZADZIAŁA, BO SESJA JEST OTWARTA
            if (tx != null) tx.rollback();
            e.printStackTrace();
            showAlert("Błąd", "Brak towaru lub błąd bazy: " + e.getMessage());
        } finally {
            session.close(); // Zamykamy sesję ręcznie na samym końcu
        }
    }

    // ... fragment metody allocateItems w OrderAddController ...
    private void allocateItems(Session session, Order order) {
        TextField[] partFields = {part1Field, part2Field, part3Field, part4Field, part5Field, part6Field, part7Field, part8Field, part9Field, part10Field};
        TextField[] qtyFields = {quantity1Field, quantity2Field, quantity3Field, quantity4Field, quantity5Field, quantity6Field, quantity7Field, quantity8Field, quantity9Field, quantity10Field};

        for (int i = 0; i < partFields.length; i++) {
            String partNr = partFields[i].getText().trim();
            String qtyStr = qtyFields[i].getText().trim();

            if (!partNr.isEmpty() && !qtyStr.isEmpty()) {
                int remainingToAllocate = Integer.parseInt(qtyStr);

                // Pobieramy dostępne sztuki (status PUTTED lub RETURNED)
                Query<Part> query = session.createQuery("FROM Part WHERE partNr = :nr AND quantity > 0 AND (status = models.PartStatus.PUTTED OR status = models.PartStatus.RETURNED) ORDER BY quantity DESC", Part.class);
                query.setParameter("nr", partNr);
                List<Part> availableStocks = query.list();

                for (Part stock : availableStocks) {
                    if (remainingToAllocate <= 0) break;

                    int take = (int) Math.min(stock.getQuantity(), remainingToAllocate);

                    // TWORZYMY POZYCJĘ ZAMÓWIENIA
                    OrderList item = new OrderList();
                    item.setOrder(order);
                    item.setPart(stock);
                    item.setQuantity(take);
                    item.setSubmit(0); // Jeszcze nie zebrane
                    session.save(item);

                    // REZERWACJA: Odejmujemy ze stanu głównego od razu
                    stock.setQuantity(stock.getQuantity() - take);
                    if (stock.getQuantity() == 0) {
                        stock.setStatus(models.PartStatus.OUT_OF_STOCK);
                    }
                    session.update(stock);

                    remainingToAllocate -= take;
                }

                if (remainingToAllocate > 0) {
                    throw new RuntimeException("Brak towaru na magazynie dla numeru: " + partNr);
                }
            }
        }
    }

    private void clearAllFields() {
        clientField.clear();
        userIDField.clear();
        TextField[] allFields = {
                part1Field, part2Field, part3Field, part4Field, part5Field, part6Field, part7Field, part8Field, part9Field, part10Field,
                quantity1Field, quantity2Field, quantity3Field, quantity4Field, quantity5Field, quantity6Field, quantity7Field, quantity8Field, quantity9Field, quantity10Field
        };
        for (TextField field : allFields) { if (field != null) field.clear(); }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null);
        alert.setContentText(content); alert.showAndWait();
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
