package controllers.WorkersPanelsControllers;
import database.PartDAO;
import database.PlacementDAO;
import models.Placement;
import org.example.DatabaseTest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Part;
import javafx.scene.paint.Color;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;


import java.io.IOException;
import java.sql.SQLException;

public class PutPanelController {
    @FXML
    private TextField part_nrField;
    @FXML
    private TextField locationField;

    @FXML
    private Button saveBut;
    @FXML
    private Button menuBut;
    @FXML
    private Label statusLabel;


    @FXML
    private void putAwayPart() {
        String location = locationField.getText().trim();
        String partNRStr = part_nrField.getText().trim();

        // 1. Walidacja pól
        if (location.isEmpty() || partNRStr.isEmpty()) {
            updateStatus("Wypełnij wszystkie pola!", Color.RED);
            return;
        }

        Transaction transaction = null;
        // Używamy SessionFactory z Twojej klasy bazowej (np. DatabaseTest lub HibernateUtil)
        try (Session session = org.example.DatabaseTest.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // 2. Szukamy części w bazie po numerze part_nr
            String hql = "FROM Part WHERE part_nr = :p_nr";
            Query<Part> query = session.createQuery(hql, Part.class);
            query.setParameter("p_nr", partNRStr);

            Part part = query.uniqueResult();

            if (part != null) {
                // 3. Aktualizujemy status części w tabeli 'part'
                part.setStatus("putted");
                session.update(part);

                // 4. Tworzymy nowy obiekt Placement (rezerwacja_miejsca)
                // Używamy Twojego konstruktora: Placement(location_nr, part_nr, part_id, quantity, user_id)
                Placement placement = new Placement(
                        location,                        // location_nr
                        part.getPart_nr(),               // part_nr
                        Integer.parseInt(part.getPart_id()), // Konwersja String part_id na int
                        part.getQuantity().intValue(),   // Konwersja Long quantity na int
                        1                                // user_id (na razie na sztywno, docelowo z sesji użytkownika)
                );

                // 5. Zapisujemy nową lokację przez sesję (możesz użyć session.save lub swojego DAO)
                session.save(placement);

                transaction.commit();

                updateStatus("Sukces! Produkt " + partNRStr + " odłożony na " + location, Color.GREEN);
                clearFields();
            } else {
                updateStatus("Błąd: Nie znaleziono części " + partNRStr, Color.RED);
            }

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            updateStatus("Błąd: " + e.getMessage(), Color.RED);
        }
    }


    public void menubutAction (ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/WorkersPanels/menuPanel.fxml"));

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



