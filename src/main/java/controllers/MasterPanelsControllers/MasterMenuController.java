package controllers.MasterPanelsControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class MasterMenuController {
    @FXML
    private Button addorderBut;
    @FXML
    private Button editorderBut;
    @FXML
    private Button menuBut;
    @FXML
    private Button loginBut;

    public void addorderbutAction(ActionEvent event)throws Exception{

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/OrderMasterPanel/MorderADDPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("AddOrderPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage = (Stage) addorderBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editorderbutAction(ActionEvent event)throws Exception{

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/OrderMasterPanel/MorderEditPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("EditOrderPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage = (Stage) editorderBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void menubutAction(ActionEvent event) throws Exception{

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/WorkersPanels/menuPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("menuPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage = (Stage) menuBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loginbutAction(ActionEvent event) throws Exception{

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/WorkersPanels/LoginPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("LoginPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage = (Stage) loginBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
