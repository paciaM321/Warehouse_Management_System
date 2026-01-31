package controllers.PutPanelsControllers;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import models.Part;
import database.PartDAO;

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
import java.util.ArrayList;
import java.util.List;

public class AddPartController {
    @FXML
    private Button addBut;
    @FXML
    private Button menuBut;
    @FXML
    private TextField partNameField;
    @FXML
    private TextField partNrField;
    @FXML
    private TextField quantityField;
    @FXML
    private Label partID;




    // Wewnątrz AddPartController
    private PartDAO partDAO = new PartDAO();

    @FXML
    public void AddPartToStorage(ActionEvent event) {
        try {
            String inputPName = partNameField.getText();
            String inputPNr = partNrField.getText();
            long inputQuantity = Long.parseLong(quantityField.getText());

            // Tworzymy obiekt (status ustawiony w konstruktorze na "PROCESSING")
            Part newPart = new Part(inputPNr, inputPName, inputQuantity);

            // Zapisujemy do bazy przez Hibernate
            partDAO.savePart(newPart);

            // Hibernate uzupełnił ID, więc możemy je wyświetlić
            partID.setText(String.valueOf(newPart.getId()));

            PauseTransition pause = new PauseTransition(Duration.seconds(1.75));
            pause.setOnFinished(event2 -> clearFields());
            pause.play();

            System.out.println("Zapisano w DB! ID: " + newPart.getId() + ", Status: " + newPart.getStatus());

        } catch (NumberFormatException e) {
            partID.setText("Błąd: Niepoprawna ilość!");
        }
    }

    private void clearFields() {
        partNameField.clear();
        partNrField.clear();
        quantityField.clear();
        partID.setText("");
    }

    public void menubutAction(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/PutMasterPanels/PutMenuPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("menu");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) menuBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
