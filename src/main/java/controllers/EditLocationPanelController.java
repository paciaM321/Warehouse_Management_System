package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class EditLocationPanelController {
   @FXML
   private Button submitBut;
    @FXML
    private Button saveBut;
    @FXML
    private Button menuBut;


    public void savebutAction (ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/AdminPanels/AdminPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("AddUserPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage = (Stage) saveBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void menubutAction (ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/menuPanel.fxml"));

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
