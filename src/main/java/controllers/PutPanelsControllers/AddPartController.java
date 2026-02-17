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
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class AddPartController {
    @FXML private Button addBut, menuBut;
    @FXML private TextField partNameField, partNrField, quantityField;
    @FXML private Label partID;

    private PartDAO partDAO = new PartDAO();

    @FXML
    public void AddPartToStorage(ActionEvent event) {
        StringBuilder errorMessages = new StringBuilder();

        //  Pobranie i trimowanie danych (usuwamy zbędne spacje)
        String inputPName = partNameField.getText().trim();
        String inputPNr = partNrField.getText().trim();
        String quantityStr = quantityField.getText().trim();

        //  Walidacja pustych elementów
        if (inputPName.isEmpty()) {
            errorMessages.append("- Nazwa produktu nie może być pusta.\n");
        }
        if (inputPNr.isEmpty()) {
            errorMessages.append("- Numer produktu (Part Nr) nie może być pusty.\n");
        }

        //  Walidacja ilościowa
        long inputQuantity = 0;
        if (quantityStr.isEmpty()) {
            errorMessages.append("- Pole 'ilość' nie może być puste.\n");
        } else {
            try {
                inputQuantity = Long.parseLong(quantityStr);
                if (inputQuantity <= 0) {
                    errorMessages.append("- Ilość musi być większa od zera.\n");
                }
            } catch (NumberFormatException e) {
                errorMessages.append("- Ilość musi być poprawną liczbą całkowitą.\n");
            }
        }

        //  Sprawdzenie, czy są błędy
        if (errorMessages.length() > 0) {
            showAlert("Błąd walidacji", errorMessages.toString());
            return;
        }

        //  Zapis do bazy danych, jeśli walidacja przeszła pomyślnie
        try {
            Part newPart = new Part(inputPNr, inputPName, inputQuantity);
            partDAO.savePart(newPart);

            partID.setText("Zapisano! ID: " + newPart.getId());
            partID.setStyle("-fx-text-fill: green;");

            // Automatyczne czyszczenie po sukcesie
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event2 -> clearFields());
            pause.play();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Błąd bazy danych", "Nie udało się zapisać produktu. Spróbuj ponownie.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        partNameField.clear();
        partNrField.clear();
        quantityField.clear();
        partID.setText("");
        partID.setStyle("-fx-text-fill: black;");
    }

    @FXML
    public void menubutAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/PutMasterPanels/PutMenuPanel.fxml"));
            Stage stage = (Stage) menuBut.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}