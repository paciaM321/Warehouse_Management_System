package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminDelUserPanelController {
    @FXML
    private Button deleteBut;
    @FXML
    private Button adminmenuBut;


    public void deletebutAction (ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/AdminPanels/AdminPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("DelUserPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage = (Stage) deleteBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void adminMenubutAction (ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/AdminPanels/AdminPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("MenuPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage = (Stage) adminmenuBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
