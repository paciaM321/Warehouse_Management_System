package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import java.io.IOException;

public class LoginController {



    @FXML
    private Button loginBut;

   public void loginNextPage (ActionEvent event) throws Exception{
       try {
           Parent root = FXMLLoader.load(getClass().getResource("/view/menuPanel.fxml"));

           Stage stage = new Stage();
           stage.setTitle("MenuPanel");
           stage.setScene(new Scene(root));
           stage.show();

           Stage mainStage=(Stage) loginBut.getScene().getWindow();
           mainStage.hide();


       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   @FXML
    private TextField loginField;
   @FXML
    private PasswordField passwordField;




}
