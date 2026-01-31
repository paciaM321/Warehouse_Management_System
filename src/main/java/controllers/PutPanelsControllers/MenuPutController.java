package controllers.PutPanelsControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuPutController {
@FXML
private Button putBut;
@FXML
private Button addpartBut;
@FXML
private Button editinfpartBut;
@FXML
private Button logoutBut;

    public void addpartAction(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/PutMasterPanels/AddPartPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("ADDPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) addpartBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editinfpartAction(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/PutMasterPanels/EditPartPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("Edit");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) editinfpartBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public void putAction(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/PutMasterPanels/putPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("PutPanel");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) putBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    public void logoutAction(ActionEvent event) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/WorkersPanels/LoginPanel.fxml"));

            Stage stage = new Stage();
            stage.setTitle("exit");
            stage.setScene(new Scene(root));
            stage.show();


            Stage mainStage=(Stage) logoutBut.getScene().getWindow();
            mainStage.hide();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
