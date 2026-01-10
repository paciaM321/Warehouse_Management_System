
package controllers;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginController {

    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginBut;
    @FXML
    private Label messageLabel; // Pamiętaj, aby dodać Label w Scene Builderze i nadać mu fx:id="messageLabel"

    // 1. Pola klasy muszą być tutaj, nie wewnątrz metod
    private List<User> userDatabase = new ArrayList<>();

    // 2. Metoda initialize uruchamia się automatycznie przy starcie okna
    public void initialize() {
        // Dodajemy przykładowych użytkowników
        userDatabase.add(new User("admin", "1234", "ADMIN"));
        userDatabase.add(new User("jan", "haslo1", "USER"));
    }

    // 3. Główna metoda obsługująca kliknięcie przycisku
    @FXML
    protected void login() {
        String inputLogin = loginField.getText();
        String inputHaslo = passwordField.getText();

        // Sprawdzamy autentykację (używamy małych liter dla nazw zmiennych)
        User loggedInUser = authenticate(inputLogin, inputHaslo);

        if (loggedInUser != null) {
            if (messageLabel != null) messageLabel.setText("Zalogowano jako: " + loggedInUser.getRole());
            przejdzDoAplikacji(loggedInUser);
        } else {
            if (messageLabel != null) messageLabel.setText("Błędny login lub hasło!");
        }
    }

    // 4. Metoda weryfikująca - musi mieć 'return'!
    private User authenticate(String login, String haslo) {
        return userDatabase.stream()
                .filter(u -> u.getLogin().equals(login) && u.getHaslo().equals(haslo))
                .findFirst()
                .orElse(null);
    }

    private void przejdzDoAplikacji(User user) {
        if (user.getRole().equals("ADMIN")) {
            messageLabel.setText("Zalogowano pomyślnie jako Administrator. Zaraz nastąpi przekierowanie...");
            messageLabel.setStyle("-fx-text-fill: green;");

            PauseTransition pause = new PauseTransition(Duration.seconds(4));
            pause.setOnFinished(event -> loginNextPage());
            pause.play();
        } else {
            messageLabel.setText("Zalogowano pomyślnie. Zaraz nastąpi przekierowanie...");
            messageLabel.setStyle("-fx-text-fill: green;");

            PauseTransition pause = new PauseTransition(Duration.seconds(4));
            pause.setOnFinished(event -> loginNextPage());
            pause.play();
        }
    }

    private void loginNextPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/menuPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("Menu");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) loginBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
